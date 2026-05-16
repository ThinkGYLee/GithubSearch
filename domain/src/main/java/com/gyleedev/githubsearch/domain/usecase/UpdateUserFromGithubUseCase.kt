package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.UserSyncResult
import com.gyleedev.githubsearch.domain.model.UserUpdateResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

private const val ACCESS_TIMEOUT_MS = 3_600_000L

// TODO fetch 상세가 바뀐거에 맞춰서 로직 수정할 것
/*
    Detail 에서 유저정보를 호출해서 업데이트 하는 함수
    accesstime 을 확인하여 캐싱
    1. AccessTime 을 Local 에서 확인한다.
    lastAccess 확인 캐싱 처리 결정 단계
    case1 : 시간 캐싱 필요 -> 전체 싱크 시작
    case2 : 시간 캐싱 필요x repo체크 기록x -> repo 싱크 시작
    case3 : 시간 캐싱 필요x repo체크 기록o -> 싱크 x
    case4 : lastAccess 가 없다 -> 잘못된 접근 -> user정보도 찾아서 user 삭제

    위에서 로직이 추가될 부분은
    1. fetch 결과가 없다,
        - user 결과가 없다. -> 일단 놔둠(fail)
        - repo 결과가 없다. -> 그냥 insert 건너 뛴다.
    2. exception이 난다. -> 그대로 fail 리턴

    여기까지 testable 하게 코드 수정하고 test code 짜기

    // TODO 이하의 내용은 정리해서 나중에 상세를 정하여 refactoring
    2. 유저, 레포 싱크단계 <- 병렬처리 안하기로
    필요한 싱크 항목을 병렬로 sync 한다.
    case1
    성공 -> accessTime update
    실패 or Exception -> 업데이트 하다가 끊긴다면? Local이 멈춘다면?
    => 둘다 업데이트가 필요할 때는 transaction 을 사용해서 싱크하도록 함.

    //Repository 는 향후 RemoteMediator 를 통해서 캐싱처리 할거기 때문에 간략하게 캐싱한다.(추가 수정하지 않겠음)
    현재 repository 관련 이슈(onConflict -> Replace 상태)

    user 캐싱을 했는데 web에 기록이 없다 -> 존재하지 않게된 유저(탈퇴) [처리방법 고민]
        - 일단은 예외처리(Exception 으로 Fail 처리)
        - 장기적으로 리팩터링 lastAccess 에 탈퇴정보 추가, 탈퇴면 fetch 하지 않도록
 */
class UpdateUserFromGithubUseCase @Inject constructor(
    private val repository: GitHubRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(id: String): UserUpdateResult = try {
        // 1. AccessTime 을 Local 에서 확인한다.
        val lastAccess = repository.getLastAccessById(id)

        if (lastAccess == null) {
            // case4 : lastAccess 가 없다 -> 잘못된 접근 -> user정보도 찾아서 user 삭제
            repository.deleteUserById(githubId = id)
            UserUpdateResult.Fail
        } else {
            val durationSinceLastAccess = Instant.now(clock).toEpochMilli() - lastAccess.accessTime.toEpochMilli()

            val isTimeOut = durationSinceLastAccess >= ACCESS_TIMEOUT_MS
            val isRepoNeedSync = !lastAccess.isRepoFetched

            when {
                // case1 : 시간 캐싱 필요 -> 전체 싱크 시작
                isTimeOut -> {
                    // user sync
                    val userSync = repository.syncUserData(id)

                    if (userSync is UserSyncResult.Success) {
                        // repo sync
                        // repo 결과가 없다 -> 그냥 insert 건너 뛴다. (repository 내부에서 처리)
                        repository.syncRepoDataList(entityId = userSync.entityId, githubId = id)

                        // accessTime update
                        repository.upsertAccessTime(id = lastAccess.id, githubId = id, isRepoFetched = true)
                        UserUpdateResult.Success
                    } else {
                        // user 결과가 없다. -> 일단 놔둠(fail)
                        UserUpdateResult.Fail
                    }
                }

                // case2 : 시간 캐싱 필요x repo체크 기록x -> repo 싱크 시작
                isRepoNeedSync -> {
                    val entityId = repository.getUserId(id)
                    if (entityId != null) {
                        repository.syncRepoDataList(entityId, id)
                    }

                    repository.upsertAccessTime(id = lastAccess.id, githubId = id, isRepoFetched = true)
                    UserUpdateResult.Success
                }

                // case3 : 시간 캐싱 필요x repo체크 기록o -> 싱크 x
                else -> {
                    UserUpdateResult.Success
                }
            }
        }
    } catch (e: Exception) {
        // 2. exception이 난다. -> 그대로 fail 리턴
        UserUpdateResult.Fail
    }
}

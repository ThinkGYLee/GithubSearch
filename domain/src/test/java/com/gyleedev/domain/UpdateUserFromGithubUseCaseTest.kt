package com.gyleedev.domain

import com.gyleedev.githubsearch.domain.model.AccessTime
import com.gyleedev.githubsearch.domain.model.UserSyncResult
import com.gyleedev.githubsearch.domain.model.UserUpdateResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.usecase.UpdateUserFromGithubUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class UpdateUserFromGithubUseCaseTest {

    @get:Rule
    val rule = MockKRule(this)

    @MockK(relaxed = true)
    lateinit var repository: GitHubRepository
    private lateinit var useCase: UpdateUserFromGithubUseCase
    private val clock = Clock.fixed(Instant.parse("2026-05-14T10:00:00Z"), ZoneId.of("UTC"))
    private val userId = "testUser"

    @Before
    fun setUp() {
        useCase = UpdateUserFromGithubUseCase(repository, clock)
    }

    @Test
    fun `마지막 접근 기록이 없으면 유저를 삭제하고 실패를 반환한다`() = runTest {
        // Given
        val expectedLastAccessTime = null
        val expectedResult = UserUpdateResult.Fail

        coEvery { repository.getLastAccessById(userId) } returns expectedLastAccessTime
        coEvery { repository.deleteUserById(userId) } just runs
        coEvery { repository.syncUserData(any()) } returns UserSyncResult.Fail
        coEvery { repository.syncRepoDataList(any(), any()) } just runs
        coEvery { repository.upsertAccessTime(any(), any(), any()) } just runs
        coEvery { repository.getUserId(any()) } returns 0L

        // When
        val actualResult = useCase(userId)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.getLastAccessById(userId) }
        coVerify(exactly = 1) { repository.deleteUserById(userId) }
        coVerify(exactly = 0) { repository.syncUserData(any()) }
        coVerify(exactly = 0) { repository.syncRepoDataList(any(), any()) }
        coVerify(exactly = 0) { repository.upsertAccessTime(any(), any(), any()) }
        coVerify(exactly = 0) { repository.getUserId(any()) }
    }

    @Test
    fun `시간이 만료되었고 유저 싱크가 성공하면 레포 싱크와 시간을 갱신하고 성공을 반환한다`() = runTest {
        // Given
        val expectedResult = UserUpdateResult.Success
        val expectedEntityId = 1L
        val lastAccess = AccessTime(
            id = expectedEntityId,
            githubId = userId,
            // 2시간 전 (Timeout)
            accessTime = Instant.parse("2026-05-14T08:00:00Z"),
            isRepoFetched = true,
        )

        coEvery { repository.getLastAccessById(userId) } returns lastAccess
        coEvery { repository.deleteUserById(any()) } just runs
        coEvery { repository.syncUserData(userId) } returns UserSyncResult.Success(entityId = expectedEntityId)
        coEvery { repository.syncRepoDataList(expectedEntityId, userId) } just runs
        coEvery { repository.upsertAccessTime(expectedEntityId, userId, true) } just runs
        coEvery { repository.getUserId(any()) } returns 0L

        // When
        val actualResult = useCase(userId)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.getLastAccessById(userId) }
        coVerify(exactly = 0) { repository.deleteUserById(any()) }
        coVerify(exactly = 1) { repository.syncUserData(userId) }
        coVerify(exactly = 1) { repository.syncRepoDataList(expectedEntityId, userId) }
        coVerify(exactly = 1) { repository.upsertAccessTime(expectedEntityId, userId, true) }
        coVerify(exactly = 0) { repository.getUserId(any()) }
    }

    @Test
    fun `시간이 만료되었으나 유저 싱크가 실패하면 실패를 반환한다`() = runTest {
        // Given
        val expectedResult = UserUpdateResult.Fail
        val lastAccess = AccessTime(
            id = 1L,
            githubId = userId,
            // 2시간 전 (Timeout)
            accessTime = Instant.parse("2026-05-14T08:00:00Z"),
            isRepoFetched = true,
        )

        coEvery { repository.getLastAccessById(userId) } returns lastAccess
        coEvery { repository.deleteUserById(any()) } just runs
        coEvery { repository.syncUserData(userId) } returns UserSyncResult.Fail
        coEvery { repository.syncRepoDataList(any(), any()) } just runs
        coEvery { repository.upsertAccessTime(any(), any(), any()) } just runs
        coEvery { repository.getUserId(any()) } returns 0L

        // When
        val actualResult = useCase(userId)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.getLastAccessById(userId) }
        coVerify(exactly = 0) { repository.deleteUserById(any()) }
        coVerify(exactly = 1) { repository.syncUserData(userId) }
        coVerify(exactly = 0) { repository.syncRepoDataList(any(), any()) }
        coVerify(exactly = 0) { repository.upsertAccessTime(any(), any(), any()) }
        coVerify(exactly = 0) { repository.getUserId(any()) }
    }

    @Test
    fun `시간이 만료되지 않았으나 레포 싱크가 필요하면 레포를 싱크하고 시간을 갱신하고 성공을 반환한다`() = runTest {
        // Given
        val expectedResult = UserUpdateResult.Success
        val expectedEntityId = 1L
        val lastAccess = AccessTime(
            id = expectedEntityId,
            githubId = userId,
            // 30분 전 (No Timeout)
            accessTime = Instant.parse("2026-05-14T09:30:00Z"),
            isRepoFetched = false,
        )

        coEvery { repository.getLastAccessById(userId) } returns lastAccess
        coEvery { repository.deleteUserById(any()) } just runs
        coEvery { repository.syncUserData(any()) } returns UserSyncResult.Fail
        coEvery { repository.syncRepoDataList(expectedEntityId, userId) } just runs
        coEvery { repository.upsertAccessTime(expectedEntityId, userId, true) } just runs
        coEvery { repository.getUserId(userId) } returns expectedEntityId

        // When
        val actualResult = useCase(userId)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.getLastAccessById(userId) }
        coVerify(exactly = 0) { repository.deleteUserById(any()) }
        coVerify(exactly = 0) { repository.syncUserData(any()) }
        coVerify(exactly = 1) { repository.getUserId(userId) }
        coVerify(exactly = 1) { repository.syncRepoDataList(expectedEntityId, userId) }
        coVerify(exactly = 1) { repository.upsertAccessTime(expectedEntityId, userId, true) }
    }

    @Test
    fun `싱크가 모두 완료된 상태이고 시간도 만료되지 않았으면 추가 동작 없이 성공을 반환한다`() = runTest {
        // Given
        val expectedResult = UserUpdateResult.Success
        val lastAccess = AccessTime(
            id = 1L,
            githubId = userId,
            // 30분 전 (No Timeout)
            accessTime = Instant.parse("2026-05-14T09:30:00Z"),
            isRepoFetched = true,
        )

        coEvery { repository.getLastAccessById(userId) } returns lastAccess
        coEvery { repository.deleteUserById(any()) } just runs
        coEvery { repository.syncUserData(any()) } returns UserSyncResult.Fail
        coEvery { repository.syncRepoDataList(any(), any()) } just runs
        coEvery { repository.upsertAccessTime(any(), any(), any()) } just runs
        coEvery { repository.getUserId(any()) } returns 0L

        // When
        val actualResult = useCase(userId)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.getLastAccessById(userId) }
        coVerify(exactly = 0) { repository.deleteUserById(any()) }
        coVerify(exactly = 0) { repository.syncUserData(any()) }
        coVerify(exactly = 0) { repository.getUserId(any()) }
        coVerify(exactly = 0) { repository.syncRepoDataList(any(), any()) }
        coVerify(exactly = 0) { repository.upsertAccessTime(any(), any(), any()) }
    }

    @Test
    fun `마지막 접근 기록 조회 시 예외가 발생하면 실패를 반환한다`() = runTest {
        // Given
        val expectedResult = UserUpdateResult.Fail

        coEvery { repository.getLastAccessById(userId) } throws RuntimeException("Database error")
        coEvery { repository.deleteUserById(any()) } just runs
        coEvery { repository.syncUserData(any()) } returns UserSyncResult.Fail
        coEvery { repository.syncRepoDataList(any(), any()) } just runs
        coEvery { repository.upsertAccessTime(any(), any(), any()) } just runs
        coEvery { repository.getUserId(any()) } returns 0L

        // When
        val actualResult = useCase(userId)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.getLastAccessById(userId) }
        coVerify(exactly = 0) { repository.deleteUserById(any()) }
        coVerify(exactly = 0) { repository.syncUserData(any()) }
        coVerify(exactly = 0) { repository.getUserId(any()) }
        coVerify(exactly = 0) { repository.syncRepoDataList(any(), any()) }
        coVerify(exactly = 0) { repository.upsertAccessTime(any(), any(), any()) }
    }

    @Test
    fun `유저 싱크 시 예외가 발생하면 실패를 반환한다`() = runTest {
        // Given
        val expectedResult = UserUpdateResult.Fail
        val lastAccess = AccessTime(
            id = 1L,
            githubId = userId,
            accessTime = Instant.parse("2026-05-14T08:00:00Z"),
            isRepoFetched = true,
        )

        coEvery { repository.getLastAccessById(userId) } returns lastAccess
        coEvery { repository.syncUserData(userId) } throws RuntimeException("Network error")
        coEvery { repository.deleteUserById(any()) } just runs
        coEvery { repository.syncRepoDataList(any(), any()) } just runs
        coEvery { repository.upsertAccessTime(any(), any(), any()) } just runs
        coEvery { repository.getUserId(any()) } returns 0L

        // When
        val actualResult = useCase(userId)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.getLastAccessById(userId) }
        coVerify(exactly = 1) { repository.syncUserData(userId) }
        coVerify(exactly = 0) { repository.deleteUserById(any()) }
        coVerify(exactly = 0) { repository.getUserId(any()) }
        coVerify(exactly = 0) { repository.syncRepoDataList(any(), any()) }
        coVerify(exactly = 0) { repository.upsertAccessTime(any(), any(), any()) }
    }

    @Test
    fun `레포 싱크 시 예외가 발생하면 실패를 반환한다`() = runTest {
        // Given
        val expectedResult = UserUpdateResult.Fail
        val expectedEntityId = 1L
        val lastAccess = AccessTime(
            id = expectedEntityId,
            githubId = userId,
            accessTime = Instant.parse("2026-05-14T08:00:00Z"),
            isRepoFetched = true,
        )

        coEvery { repository.getLastAccessById(userId) } returns lastAccess
        coEvery { repository.syncUserData(userId) } returns UserSyncResult.Success(entityId = expectedEntityId)
        coEvery { repository.syncRepoDataList(expectedEntityId, userId) } throws RuntimeException("Sync error")
        coEvery { repository.deleteUserById(any()) } just runs
        coEvery { repository.upsertAccessTime(any(), any(), any()) } just runs
        coEvery { repository.getUserId(any()) } returns 0L

        // When
        val actualResult = useCase(userId)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.getLastAccessById(userId) }
        coVerify(exactly = 1) { repository.syncUserData(userId) }
        coVerify(exactly = 1) { repository.syncRepoDataList(expectedEntityId, userId) }
        coVerify(exactly = 0) { repository.deleteUserById(any()) }
        coVerify(exactly = 0) { repository.getUserId(any()) }
        coVerify(exactly = 0) { repository.upsertAccessTime(any(), any(), any()) }
    }

    @Test
    fun `접근 시간 갱신 시 예외가 발생하면 실패를 반환한다`() = runTest {
        // Given
        val expectedResult = UserUpdateResult.Fail
        val expectedEntityId = 1L
        val lastAccess = AccessTime(
            id = expectedEntityId,
            githubId = userId,
            accessTime = Instant.parse("2026-05-14T08:00:00Z"),
            isRepoFetched = true,
        )

        coEvery { repository.getLastAccessById(userId) } returns lastAccess
        coEvery { repository.syncUserData(userId) } returns UserSyncResult.Success(entityId = expectedEntityId)
        coEvery { repository.syncRepoDataList(expectedEntityId, userId) } just runs
        coEvery { repository.upsertAccessTime(expectedEntityId, userId, true) } throws RuntimeException("Update error")
        coEvery { repository.deleteUserById(any()) } just runs
        coEvery { repository.getUserId(any()) } returns 0L

        // When
        val actualResult = useCase(userId)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.getLastAccessById(userId) }
        coVerify(exactly = 1) { repository.syncUserData(userId) }
        coVerify(exactly = 1) { repository.syncRepoDataList(expectedEntityId, userId) }
        coVerify(exactly = 1) { repository.upsertAccessTime(expectedEntityId, userId, true) }
        coVerify(exactly = 0) { repository.deleteUserById(any()) }
        coVerify(exactly = 0) { repository.getUserId(any()) }
    }

    @Test
    fun `유저 ID 조회 시 예외가 발생하면 실패를 반환한다`() = runTest {
        // Given
        val expectedResult = UserUpdateResult.Fail
        val lastAccess = AccessTime(
            id = 1L,
            githubId = userId,
            accessTime = Instant.parse("2026-05-14T09:30:00Z"),
            isRepoFetched = false,
        )

        coEvery { repository.getLastAccessById(userId) } returns lastAccess
        coEvery { repository.getUserId(userId) } throws RuntimeException("ID query error")
        coEvery { repository.deleteUserById(any()) } just runs
        coEvery { repository.syncUserData(any()) } returns UserSyncResult.Fail
        coEvery { repository.syncRepoDataList(any(), any()) } just runs
        coEvery { repository.upsertAccessTime(any(), any(), any()) } just runs

        // When
        val actualResult = useCase(userId)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.getLastAccessById(userId) }
        coVerify(exactly = 1) { repository.getUserId(userId) }
        coVerify(exactly = 0) { repository.deleteUserById(any()) }
        coVerify(exactly = 0) { repository.syncUserData(any()) }
        coVerify(exactly = 0) { repository.syncRepoDataList(any(), any()) }
        coVerify(exactly = 0) { repository.upsertAccessTime(any(), any(), any()) }
    }

    @Test
    fun `유저 삭제 시 예외가 발생하면 실패를 반환한다`() = runTest {
        // Given
        val expectedResult = UserUpdateResult.Fail

        coEvery { repository.getLastAccessById(userId) } returns null
        coEvery { repository.deleteUserById(userId) } throws RuntimeException("Delete error")
        coEvery { repository.syncUserData(any()) } returns UserSyncResult.Fail
        coEvery { repository.syncRepoDataList(any(), any()) } just runs
        coEvery { repository.upsertAccessTime(any(), any(), any()) } just runs
        coEvery { repository.getUserId(any()) } returns 0L

        // When
        val actualResult = useCase(userId)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.getLastAccessById(userId) }
        coVerify(exactly = 1) { repository.deleteUserById(userId) }
        coVerify(exactly = 0) { repository.syncUserData(any()) }
        coVerify(exactly = 0) { repository.getUserId(any()) }
        coVerify(exactly = 0) { repository.syncRepoDataList(any(), any()) }
        coVerify(exactly = 0) { repository.upsertAccessTime(any(), any(), any()) }
    }
}

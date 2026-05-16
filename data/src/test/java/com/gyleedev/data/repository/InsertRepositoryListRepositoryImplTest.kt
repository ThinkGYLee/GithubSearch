package com.gyleedev.data.repository

import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.database.entity.toEntity
import com.gyleedev.data.preference.TokenPreference
import com.gyleedev.data.remote.AccessService
import com.gyleedev.data.remote.GithubApiService
import com.gyleedev.data.remote.RevokeService
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Clock

class InsertRepositoryListRepositoryImplTest {
    private lateinit var repository: GitHubRepositoryImpl
    private val userDao: UserDao = mockk()
    private val reposDao: ReposDao = mockk()
    private val accessTimeDao: AccessTimeDao = mockk()
    private val githubApiService: GithubApiService = mockk()
    private val accessService: AccessService = mockk()
    private val revokeService: RevokeService = mockk()
    private val tokenPreference: TokenPreference = mockk()
    private val clock: Clock = mockk()

    @Before
    fun setUp() {
        repository = GitHubRepositoryImpl(
            userDao = userDao,
            reposDao = reposDao,
            accessTimeDao = accessTimeDao,
            githubApiService = githubApiService,
            accessService = accessService,
            revokeService = revokeService,
            tokenPreference = tokenPreference,
            clock = clock,
        )
    }

    @Test
    fun `레포지토리 리스트 저장 시 도메인 모델을 엔티티로 변환하여 ReposDao의 insertRepos를 호출한다`() = runTest {
        // Given
        val userEntityId = 1L
        val repositoryModels = listOf(
            RepositoryModel(
                name = "repo1",
                userGithubId = "test_user",
                description = "desc1",
                language = "Kotlin",
                stargazer = 10,
            ),
            RepositoryModel(
                name = "repo2",
                userGithubId = "test_user",
                description = "desc2",
                language = "Java",
                stargazer = 20,
            ),
        )
        val expectedEntities = repositoryModels.map { it.toEntity(userEntityId) }

        coEvery { reposDao.insertRepos(expectedEntities) } just runs

        // When
        repository.insertRepositoryList(userEntityId, repositoryModels)

        // Then
        coVerify(exactly = 1) { reposDao.insertRepos(expectedEntities) }
    }
}

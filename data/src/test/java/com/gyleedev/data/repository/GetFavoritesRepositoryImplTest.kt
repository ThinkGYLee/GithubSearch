package com.gyleedev.data.repository

import androidx.paging.testing.asSnapshot
import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.database.entity.UserEntity
import com.gyleedev.data.database.entity.toModel
import com.gyleedev.data.preference.TokenPreference
import com.gyleedev.data.remote.AccessService
import com.gyleedev.data.remote.GithubApiService
import com.gyleedev.data.remote.RevokeService
import com.gyleedev.data.utils.createDummyUserEntity
import com.gyleedev.data.utils.createMockPagingSource
import com.gyleedev.githubsearch.domain.model.FilterStatus
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Clock
import kotlinx.coroutines.test.runTest

class GetFavoritesRepositoryImplTest {
    private lateinit var repository: GitHubRepositoryImpl
    private val userDao: UserDao = mockk()
    private val reposDao: ReposDao = mockk()
    private val accessTimeDao: AccessTimeDao = mockk()
    private val githubApiService: GithubApiService = mockk()
    private val accessService: AccessService = mockk()
    private val revokeService: RevokeService = mockk()
    private val tokenPreference: TokenPreference = mockk()
    private val clock: Clock = mockk()

    // 10개의 고정된 더미 데이터 리스트
    private val fullUserList =
        listOf(
            createDummyUserEntity(id = 1, repoCount = 5),
            createDummyUserEntity(id = 2, repoCount = 0),
            createDummyUserEntity(id = 3, repoCount = 10),
            createDummyUserEntity(id = 4, repoCount = 0),
            createDummyUserEntity(id = 5, repoCount = 1),
            createDummyUserEntity(id = 6, repoCount = 0),
            createDummyUserEntity(id = 7, repoCount = 20),
            createDummyUserEntity(id = 8, repoCount = 0),
            createDummyUserEntity(id = 9, repoCount = 3),
            createDummyUserEntity(id = 10, repoCount = 0),
        )

    @Before
    fun setUp() {
        repository =
            GitHubRepositoryImpl(
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
    fun `즐겨찾기 목록 조회 시 FilterStatus가 ALL이면 10개의 모든 유저 데이터를 반환한다`() =
        runTest {
            // Given
            val expectedStatus = FilterStatus.ALL
            val expectedEntities = fullUserList
            val expectedModels = expectedEntities.map { it.toModel() }
            val mockPagingSource = createMockPagingSource<Int, UserEntity>(expectedEntities)

            every { userDao.getUsers(expectedStatus) } returns mockPagingSource

            // When
            val resultFlow = repository.getFavorites(expectedStatus)
            val actualResult = resultFlow.asSnapshot()

            // Then
            assertEquals(expectedModels, actualResult)
            coVerify(exactly = 1) { userDao.getUsers(expectedStatus) }
        }

    @Test
    fun `즐겨찾기 목록 조회 시 FilterStatus가 REPO이면 레포지토리가 있는 유저들만 반환한다`() =
        runTest {
            // Given
            val expectedStatus = FilterStatus.REPO
            val expectedEntities = fullUserList.filter { it.repoCount > 0 }
            val expectedModels = expectedEntities.map { it.toModel() }
            val mockPagingSource = createMockPagingSource<Int, UserEntity>(expectedEntities)

            every { userDao.getUsers(expectedStatus) } returns mockPagingSource

            // When
            val resultFlow = repository.getFavorites(expectedStatus)
            val actualResult = resultFlow.asSnapshot()

            // Then
            assertEquals(expectedModels, actualResult)
            coVerify(exactly = 1) { userDao.getUsers(expectedStatus) }
        }

    @Test
    fun `즐겨찾기 목록 조회 시 FilterStatus가 NOREPO이면 레포지토리가 없는 유저들만 반환한다`() =
        runTest {
            // Given
            val expectedStatus = FilterStatus.NOREPO
            val expectedEntities = fullUserList.filter { it.repoCount == 0 }
            val expectedModels = expectedEntities.map { it.toModel() }
            val mockPagingSource = createMockPagingSource<Int, UserEntity>(expectedEntities)

            every { userDao.getUsers(expectedStatus) } returns mockPagingSource

            // When
            val resultFlow = repository.getFavorites(expectedStatus)
            val actualResult = resultFlow.asSnapshot()

            // Then
            assertEquals(expectedModels, actualResult)
            coVerify(exactly = 1) { userDao.getUsers(expectedStatus) }
        }

    @Test
    fun `즐겨찾기 목록이 비어있을 때 빈 리스트를 정상적으로 반환한다`() =
        runTest {
            // Given
            val expectedStatus = FilterStatus.ALL
            val expectedEntities = emptyList<UserEntity>()
            val expectedModels = emptyList<com.gyleedev.githubsearch.domain.model.UserModel>()
            val mockPagingSource = createMockPagingSource<Int, UserEntity>(expectedEntities)

            every { userDao.getUsers(expectedStatus) } returns mockPagingSource

            // When
            val resultFlow = repository.getFavorites(expectedStatus)
            val actualResult = resultFlow.asSnapshot()

            // Then
            assertEquals(expectedModels, actualResult)
            coVerify(exactly = 1) { userDao.getUsers(expectedStatus) }
        }
}

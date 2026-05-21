package com.gyleedev.data.repository

import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.database.entity.toEntity
import com.gyleedev.data.preference.TokenPreference
import com.gyleedev.data.remote.AccessService
import com.gyleedev.data.remote.GithubApiService
import com.gyleedev.data.remote.RevokeService
import com.gyleedev.githubsearch.domain.model.UserModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Clock

class InsertUserRepositoryImplTest {
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
    fun `유저 정보를 저장할 때 도메인 모델을 엔티티로 변환하여 UserDao의 upsertUser를 호출한다`() = runTest {
        // Given
        val userModel = UserModel(
            id = 1L,
            login = "test_login",
            name = "test_name",
            followers = 10,
            following = 20,
            avatar = "http://avatar.url",
            company = "Test Company",
            email = "test@email.com",
            bio = "test bio",
            repoCount = 5,
            createdDate = "2023-01-01",
            updatedDate = "2023-01-02",
            reposAddress = "http://repos.url",
            blogUrl = "http://blog.url",
            favorite = true,
        )
        val expectedEntity = userModel.toEntity()
        val expectedInsertId = 100L

        coEvery { userDao.upsertUser(expectedEntity) } returns expectedInsertId

        // When
        repository.upsertUser(userModel)

        // Then
        coVerify(exactly = 1) { userDao.upsertUser(expectedEntity) }
    }

    @Test
    fun `유저 정보 저장 시 Primary Key나 Index 중복으로 Abort 예외가 발생하면 상위로 전파한다`() = runTest {
        // Given
        val userModel = UserModel(
            id = 1L,
            login = "test_login",
            name = "test_name",
            followers = 10,
            following = 20,
            avatar = "http://avatar.url",
            company = "Test Company",
            email = "test@email.com",
            bio = "test bio",
            repoCount = 5,
            createdDate = "2023-01-01",
            updatedDate = "2023-01-02",
            reposAddress = "http://repos.url",
            blogUrl = "http://blog.url",
            favorite = true,
        )
        val expectedEntity = userModel.toEntity()
        val exception = RuntimeException("UNIQUE constraint failed: user.github_id")

        coEvery { userDao.upsertUser(expectedEntity) } throws exception

        // When
        var actualException: Exception? = null
        try {
            repository.upsertUser(userModel)
            org.junit.Assert.fail("예외가 발생해야 합니다.")
        } catch (e: Exception) {
            actualException = e
        }

        // Then
        assertEquals(exception.message, actualException?.message)
        coVerify(exactly = 1) { userDao.upsertUser(expectedEntity) }
    }
}

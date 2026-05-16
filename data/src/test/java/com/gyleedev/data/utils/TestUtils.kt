package com.gyleedev.data.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gyleedev.data.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

fun createDummyUserEntity(id: Long, repoCount: Int): UserEntity = UserEntity(
    id = id,
    name = "name_$id",
    githubId = "login_$id",
    followers = 0,
    following = 0,
    avatar = "",
    company = null,
    email = null,
    bio = null,
    repoCount = repoCount,
    createdDate = null,
    updatedDate = null,
    reposAddress = "",
    blogUrl = null,
    favorite = true,
)

fun <K : Any, V : Any> createMockPagingSource(data: List<V>): PagingSource<K, V> = object : PagingSource<K, V>() {
    override suspend fun load(params: LoadParams<K>): LoadResult<K, V> = LoadResult.Page(
        data = data,
        prevKey = null,
        nextKey = null,
    )

    override fun getRefreshKey(state: PagingState<K, V>): K? = null
}

/**
 * coVerify 블록 내부에서 Flow를 반환하는 함수 검증 시 발생하는
 * 'flow is constructed but unused' 경고를 억제하기 위한 확장 함수입니다.
 */
fun <T> Flow<T>.ignoreUnused() = Unit

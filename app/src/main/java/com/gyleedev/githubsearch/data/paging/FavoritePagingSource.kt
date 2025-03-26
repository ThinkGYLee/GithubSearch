package com.gyleedev.githubsearch.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gyleedev.githubsearch.data.database.dao.UserDao
import com.gyleedev.githubsearch.data.database.entity.UserEntity
import com.gyleedev.githubsearch.domain.model.FilterStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class FavoritePagingSource(
    private val dao: UserDao,
    private val status: FilterStatus
) : PagingSource<Int, UserEntity>() {
    override fun getRefreshKey(state: PagingState<Int, UserEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserEntity> {
        val page = params.key ?: 1
        return try {
            var data: List<UserEntity>?
            data = withContext(Dispatchers.IO) { dao.getFavorite(page, true) }
            data = when (status) {
                FilterStatus.ALL -> { data }
                FilterStatus.REPO -> { data.filter { it.repos > 0 } }
                FilterStatus.NOREPO -> { data.filter { it.repos == 0 } }
            }
            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.isEmpty()) null else page + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: retrofit2.HttpException) {
            LoadResult.Error(exception)
        }
    }
}

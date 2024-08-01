package com.gyleedev.githubsearch.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gyleedev.githubsearch.data.database.dao.UserDao
import com.gyleedev.githubsearch.data.database.entity.UserEntity
import java.io.IOException

class UserPagingSource(
    private val dao: UserDao
) : PagingSource<Int, UserEntity>() {
    override fun getRefreshKey(state: PagingState<Int, UserEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserEntity> {
        val page = params.key ?: 1
        return try {
            val data: List<UserEntity>?
           /* data = withContext(Dispatchers.IO) { dao.getUsers(page) }
            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.size < 10) null else page + 1,
            )*/
            TODO()
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: retrofit2.HttpException) {
            LoadResult.Error(exception)
        }
    }
}

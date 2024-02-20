package com.gyleedev.githubsearch.domain.usecase


import com.gyleedev.githubsearch.domain.model.DetailFeed
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.model.UserModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class GetBothUseCase @Inject constructor(
    private val userUseCase: GetUserUseCase,
    private val repositoryUseCase: GetRepositoryUseCase
) {
    suspend operator fun invoke(user: String): List<DetailFeed> {
        return withContext(Dispatchers.IO) {
            val userInfo = async { userUseCase.execute(user) }
            val repoInfo = async { repositoryUseCase.execute(user) }
            // 2개중 하나
            awaitAll(userInfo, repoInfo)
            modelToFeed(userInfo.await(), repoInfo.await())
        }
    }
}

private fun modelToFeed(userInfo: UserModel?, repoInfo: List<RepositoryModel>): List<DetailFeed> {

    val list = mutableListOf<DetailFeed>()

    if (userInfo != null) {
        val profile = DetailFeed.UserProfile(userInfo)
        val detail = DetailFeed.UserDetail(userInfo)
        list.add(profile)
        list.add(detail)
    }

    list.add(DetailFeed.RepoTitle)

    if (repoInfo.isNotEmpty()) {
        list.addAll(repoInfo.map { DetailFeed.RepoDetail(it) })
    } else {
        list.add(DetailFeed.RepoNoItem)
    }

    return list
}
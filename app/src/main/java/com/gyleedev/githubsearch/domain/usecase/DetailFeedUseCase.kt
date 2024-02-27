package com.gyleedev.githubsearch.domain.usecase


import com.gyleedev.githubsearch.data.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.model.DetailFeed
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.model.UserModel
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DetailFeedUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    suspend operator fun invoke(id: String): List<DetailFeed> {
        return withContext(Dispatchers.IO) {
            val user = repository.getDetailUser(id)
            val repo = repository.getRepositories(id)
            modelToFeed(user, repo)
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
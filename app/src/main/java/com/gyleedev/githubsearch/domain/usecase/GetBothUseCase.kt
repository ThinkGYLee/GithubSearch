package com.gyleedev.githubsearch.domain.usecase


import com.gyleedev.githubsearch.data.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.model.DetailFeed
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.model.UserModel
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class GetBothUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    suspend operator fun invoke(id: String): List<DetailFeed> {
        return withContext(Dispatchers.IO) {
            val time = repository.getLastAccessById(id)

            val user = async {
                if (time != null) {
                    if (Instant.now().toEpochMilli() - time.accessTime.toEpochMilli() < 3600000) {
                        repository.getUser(id)
                    } else {
                        repository.getUserFromGithub(id)
                    }
                } else {
                    repository.getUserFromGithub(id)
                }
            }
            val userinfo = user.await()
            val repo = async {
                repository.getRepositories(id)
            }


            // 2개중 하나
            modelToFeed(userinfo, repo.await())
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
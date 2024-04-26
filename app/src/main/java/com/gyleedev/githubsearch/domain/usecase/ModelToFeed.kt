package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.DetailFeed
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.model.UserModel

object ModelToFeed {
    fun modelToFeed(
        userInfo: UserModel?,
        repoInfo: List<RepositoryModel>?
    ): List<DetailFeed> {

        val list = mutableListOf<DetailFeed>()

        if (userInfo != null) {
            val profile = DetailFeed.UserProfile(userInfo)
            val detail = DetailFeed.UserDetail(userInfo)
            list.add(profile)
            list.add(detail)
        }

        list.add(DetailFeed.RepoTitle)

        if(repoInfo!=null) {
            if(repoInfo.isNotEmpty()) {
                list.addAll(repoInfo.map { DetailFeed.RepoDetail(it) })
            } else {
                list.add(DetailFeed.RepoNoItem)
            }
        }  else {
            list.add(DetailFeed.RepoNoItem)
        }

        return list
    }
}
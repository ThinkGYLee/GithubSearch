package com.gyleedev.githubsearch.util

import com.gyleedev.githubsearch.domain.model.SearchStatus

fun CodeToStatusUtil(
    code: Int
): SearchStatus {
    return when (code) {
        200 -> {
            SearchStatus.SUCCESS
        }

        401 -> {
            SearchStatus.NEED_AUTHENTICATION
        }

        404 -> {
            SearchStatus.NO_SUCH_USER
        }

        403 -> {
            SearchStatus.NEED_AUTHENTICATION
        }

        else -> {
            SearchStatus.BAD_NETWORK
        }
    }
}
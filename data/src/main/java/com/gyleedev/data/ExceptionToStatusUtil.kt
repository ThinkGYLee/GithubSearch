package com.gyleedev.data

import com.gyleedev.githubsearch.domain.model.SearchStatus
import retrofit2.HttpException
import java.net.UnknownHostException

fun exceptionToStatusUtil(exception: Exception): SearchStatus = when (exception) {
    is HttpException -> {
        codeToStatusUtil(exception.code())
    }

    is UnknownHostException -> {
        SearchStatus.BAD_NETWORK
    }

    else -> {
        SearchStatus.UNKNOWN_FAIL
    }
}

fun codeToStatusUtil(code: Int): SearchStatus = when (code) {
    401 -> {
        SearchStatus.NEED_AUTHENTICATION
    }

    403 -> {
        SearchStatus.NEED_AUTHENTICATION
    }

    404 -> {
        SearchStatus.NO_SUCH_USER
    }

    else -> {
        SearchStatus.UNKNOWN_FAIL
    }
}

package com.gyleedev.githubsearch.feature.detail

import java.net.URI

// 전달된 URL에서 프로토콜(http, https) 및 경로를 제외한 기본 도메인만 추출합니다.
internal fun extractDomain(
    url: String,
): String {
    try {
        val isMissingProtocol = !url.startsWith(prefix = "http://") &&
            !url.startsWith(prefix = "https://")

        val validUrl = if (isMissingProtocol) {
            "http://$url"
        } else {
            url
        }

        val uri = URI(validUrl)
        val host = uri.host

        if (host != null) {
            return host
        } else {
            return url
        }
    } catch (e: Exception) {
        return url
    }
}

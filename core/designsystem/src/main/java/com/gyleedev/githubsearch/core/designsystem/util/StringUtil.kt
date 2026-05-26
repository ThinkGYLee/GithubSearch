package com.gyleedev.githubsearch.core.designsystem.util

/**
 * Prefix로 '@'가 포함된 문자열이 들어올 경우 '@'를 제거하고 반환합니다.
 * '@'로 시작하지 않는 문자열은 그대로 반환합니다.
 */
fun String.removeAtPrefix(): String = if (this.startsWith("@")) {
    this.drop(1)
} else {
    this
}

package com.gyleedev.githubsearch.core.designsystem.util

import com.vdurmont.emoji.EmojiParser

/**
 * 텍스트 내의 GitHub 이모지 코드(예: :necktie:)를 실제 이모티콘 문자로 변환합니다.
 * emoji-java 라이브러리를 사용하여 모든 표준 GitHub 이모지를 지원합니다.
 *
 * @param text 변환할 원본 텍스트
 * @return 이모지 코드가 변환된 텍스트. text가 null인 경우 null 반환.
 */
fun String?.parseEmojis(): String? {
    if (this == null) return null
    return try {
        EmojiParser.parseToUnicode(this)
    } catch (e: Exception) {
        this
    }
}

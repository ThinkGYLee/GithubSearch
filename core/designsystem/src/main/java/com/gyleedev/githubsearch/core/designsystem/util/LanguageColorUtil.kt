package com.gyleedev.githubsearch.core.designsystem.util

import androidx.compose.ui.graphics.Color
import com.gyleedev.githubsearch.core.designsystem.theme.LanguageColorMap

object LanguageColorUtil {
    /**
     * 언어 이름을 기반으로 해당하는 컬러를 반환합니다.
     * 정의되지 않은 언어의 경우 Transparent를 반환합니다.
     */
    fun getColor(language: String?): Color = LanguageColorMap[language] ?: Color.Transparent
}

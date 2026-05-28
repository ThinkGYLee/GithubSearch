package com.gyleedev.githubsearch.core.designsystem.util

import kotlin.math.abs

/**
 * 숫자가 1000 이상일 경우 1k, 1000000 이상일 경우 1m과 같이 축약된 문자열로 반환합니다.
 * 소수점은 첫째 자리까지만 표시하며, 내림 처리하여 표기합니다 (예: 1599 -> 1.5k).
 */
fun Int.toCompactString(): String {
    val absValue = abs(this.toLong())
    val sign = if (this < 0) "-" else ""

    return when {
        absValue >= 1_000_000 -> {
            val integerPart = absValue / 1_000_000
            val decimalPart = (absValue % 1_000_000) / 100_000
            if (decimalPart > 0) {
                "${sign}$integerPart.${decimalPart}m"
            } else {
                "${sign}${integerPart}m"
            }
        }

        absValue >= 1_000 -> {
            val integerPart = absValue / 1_000
            val decimalPart = (absValue % 1_000) / 100
            if (decimalPart > 0) {
                "${sign}$integerPart.${decimalPart}k"
            } else {
                "${sign}${integerPart}k"
            }
        }

        else -> this.toString()
    }
}

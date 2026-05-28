package com.gyleedev.githubsearch.core.designsystem.util

import org.junit.Assert.assertEquals
import org.junit.Test

class NumberUtilTest {
    @Test
    fun `1000 미만의 숫자는 그대로 문자열로 반환한다`() {
        assertEquals("0", 0.toCompactString())
        assertEquals("999", 999.toCompactString())
        assertEquals("-999", (-999).toCompactString())
    }

    @Test
    fun `1000 이상 1000000 미만의 숫자는 k 단위로 반환한다`() {
        assertEquals("1k", 1000.toCompactString())
        assertEquals("1.5k", 1500.toCompactString())
        // 반올림하지 않고 버림(Truncate) 처리 확인
        assertEquals("1.5k", 1599.toCompactString())
        assertEquals("10k", 10000.toCompactString())
        assertEquals("999.9k", 999999.toCompactString())
    }

    @Test
    fun `1000000 이상의 숫자는 m 단위로 반환한다`() {
        assertEquals("1m", 1000000.toCompactString())
        assertEquals("1.5m", 1500000.toCompactString())
        // 반올림하지 않고 버림(Truncate) 처리 확인
        assertEquals("1.5m", 1599999.toCompactString())
        assertEquals("10m", 10000000.toCompactString())
    }

    @Test
    fun `음수도 동일한 로직으로 k와 m 단위를 붙여서 반환한다`() {
        assertEquals("-1k", (-1000).toCompactString())
        assertEquals("-1.5k", (-1599).toCompactString())
        assertEquals("-1m", (-1000000).toCompactString())
        assertEquals("-1.5m", (-1599999).toCompactString())
    }
}

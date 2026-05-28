package com.gyleedev.githubsearch.core.designsystem.util

import org.junit.Assert.assertEquals
import org.junit.Test

class StringUtilTest {
    @Test
    fun `문자열이 @로 시작하면 @를 제거하고 반환한다`() {
        val input = "@user123"
        val expected = "user123"

        val actual = input.removeAtPrefix()

        assertEquals(expected, actual)
    }

    @Test
    fun `문자열이 @로 시작하지 않으면 그대로 반환한다`() {
        val input = "user123"
        val expected = "user123"

        val actual = input.removeAtPrefix()

        assertEquals(expected, actual)
    }

    @Test
    fun `빈 문자열일 경우 빈 문자열을 반환한다`() {
        val input = ""
        val expected = ""

        val actual = input.removeAtPrefix()

        assertEquals(expected, actual)
    }

    @Test
    fun `@만 있는 경우 빈 문자열을 반환한다`() {
        val input = "@"
        val expected = ""

        val actual = input.removeAtPrefix()

        assertEquals(expected, actual)
    }
}

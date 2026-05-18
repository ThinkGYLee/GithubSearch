package com.gyleedev.githubsearch.core.testing

import kotlinx.coroutines.flow.Flow

/**
 * coVerify 블록 내부에서 Flow를 반환하는 함수 검증 시 발생하는
 * 'flow is constructed but unused' 경고를 억제하기 위한 확장 함수입니다.
 */
fun <T> Flow<T>.ignoreUnused() = Unit

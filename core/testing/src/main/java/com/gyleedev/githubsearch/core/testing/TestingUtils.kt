package com.gyleedev.githubsearch.core.testing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

/**
 * coVerify 블록 내부에서 Flow를 반환하는 함수 검증 시 발생하는
 * 'flow is constructed but unused' 경고를 억제하기 위한 확장 함수입니다.
 */
fun <T> Flow<T>.ignoreUnused() = Unit

/**
 * 테스트 시 Flow의 상태 변화를 리스트에 수집하기 위한 확장 함수입니다.
 * @param scope 수집을 수행할 CoroutineScope (주로 backgroundScope)
 * @param list 상태를 저장할 가변 리스트
 */
fun <T> Flow<T>.collectIn(
    scope: CoroutineScope,
    list: MutableList<T>,
) = scope.launch {
    this@collectIn.toList(list)
}

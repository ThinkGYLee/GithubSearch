package com.gyleedev.githubsearch.core.testing

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * 테스트에서 사용할 디스패처들을 제공하는 인터페이스입니다.
 */
interface TestDispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}

/**
 * 모든 디스패처가 하나의 [TestDispatcher]를 공유하도록 하는 구현체입니다.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SharedTestDispatcherProvider(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestDispatcherProvider {
    override val main: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
}

/**
 * 메인 디스패처를 교체해주는 JUnit Rule입니다.
 * NIA와 같이 UnconfinedTestDispatcher를 기본으로 사용하여 비동기 작업을 즉시 실행되게 합니다.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

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
 * 테스트에서 사용할 디스패처들을 캡슐화하는 인터페이스입니다.
 * (현재는 테스트 모듈 내부에서만 사용됩니다.)
 */
interface TestDispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}

/**
 * 모든 디스패처가 하나의 [TestDispatcher]를 공유하도록 하는 구현체입니다.
 */
class SharedTestDispatcherProvider
@OptIn(ExperimentalCoroutinesApi::class)
constructor(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestDispatcherProvider {
    override val main: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
}

/**
 * 메인 디스패처를 교체하고, 테스트용 디스패처 제공자를 관리하는 JUnit Rule입니다.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {

    val dispatcherProvider: TestDispatcherProvider = SharedTestDispatcherProvider(testDispatcher)

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

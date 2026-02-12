package com.ggomodoro.core.common.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * 코루틴 디스패처를 제공하는 인터페이스입니다.
 * 테스트 시 디스패처를 주입받아 제어하기 위해 사용합니다.
 */
interface DispatcherProvider {
    /** UI 작업을 위한 메인 디스패처 */
    val main: CoroutineDispatcher
    /** I/O 작업을 위한 IO 디스패처 */
    val io: CoroutineDispatcher
    /** CPU 집약적인 작업을 위한 Default 디스패처 */
    val default: CoroutineDispatcher
}

class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
}

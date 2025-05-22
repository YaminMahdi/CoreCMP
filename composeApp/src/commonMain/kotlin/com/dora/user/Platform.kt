package com.dora.user

import kotlinx.coroutines.CoroutineDispatcher

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


expect fun ioDispatcher() : CoroutineDispatcher

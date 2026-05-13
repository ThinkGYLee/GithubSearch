package com.gyleedev.domain

import kotlinx.coroutines.flow.Flow

fun <T> Flow<T>.ignoreUnused() = Unit

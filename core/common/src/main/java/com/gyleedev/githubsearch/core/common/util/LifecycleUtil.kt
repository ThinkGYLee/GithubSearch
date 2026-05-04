package com.gyleedev.githubsearch.core.common.util

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> AppCompatActivity.launchWithLifecycle(flow: Flow<T>, block: suspend (T) -> Unit) {
    lifecycleScope.launch {
        flow.collect {
            block(it)
        }
    }
}

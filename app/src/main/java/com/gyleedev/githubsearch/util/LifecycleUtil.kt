package com.gyleedev.githubsearch.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun Fragment.launchWithLifecycle(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(state, block)
    }
}

fun <T> Fragment.launchWithLifecycle(
    flow: Flow<T>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend (T) -> Unit
) {
    lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(state) {
            flow.collect {
                block(it)
            }
        }
    }
}

fun <T> AppCompatActivity.launchWithLifecycle(
    flow: Flow<T>,
    block: suspend (T) -> Unit
) {
    lifecycleScope.launch {
        flow.collect {
            block(it)
        }
    }
}

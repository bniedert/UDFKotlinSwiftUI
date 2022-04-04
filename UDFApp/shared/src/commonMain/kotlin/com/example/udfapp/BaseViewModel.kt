package com.example.udfapp

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface ViewState
interface Action

abstract class BaseViewModel<S : ViewState, A : Action> {
    val state by lazy { MutableStateFlow(initialState()) }

    fun watchState() = observeState().wrap()

    private fun observeState(): StateFlow<S> = state

    abstract fun initialState(): S
    abstract fun onAction(action: A)
}

fun interface Closeable {
    fun close()
}

class CFlow<T: Any> internal constructor(private val origin: Flow<T>) : Flow<T> by origin {
    fun watch(block: (T) -> Unit): Closeable {
        val job = Job()

        onEach {
            block(it)
        }.launchIn(CoroutineScope(Dispatchers.Main + job))

        return Closeable { job.cancel() }
    }
}

internal fun <T: Any> Flow<T>.wrap(): CFlow<T> = CFlow(this)

enum class LoadingState {
    Loading, Loaded, Error, None
}
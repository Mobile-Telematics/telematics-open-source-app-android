package com.telematics.core.common.provider.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

typealias EventFlow<T> = Flow<EventWrapper<T>>

class MutableEventFlow<T> {
    private val internalState = MutableStateFlow<EventWrapper<T>?>(null)

    val asFlow: EventFlow<T>
        get() = internalState.filterNotNull()

    @Synchronized
    fun postEvent(event: T) {
        internalState.value = EventWrapper(event)
    }

    @Synchronized
    fun postEventOnce(event: T) {
        if (internalState.value == null) {
            postEvent(event)
        }
    }
}

/**
 * Convenience extensions to reduce boilerplate code on subscribing and setting.
 */
fun <T> EventFlow<T>.subscribe(scope: CoroutineScope, consumer: (T) -> Unit) {
    onEach { it.consumeEvent(consumer) }.launchIn(scope)
}

/**
 * Convenience extensions for case when there is no payload in event.
 */
fun EventFlow<Unit>.subscribe(scope: CoroutineScope, action: () -> Unit) {
    subscribe<Unit>(scope) { action() }
}

fun MutableEventFlow<Unit>.postEvent() = postEvent(Unit)
fun MutableEventFlow<Unit>.postEventOnce() = postEventOnce(Unit)

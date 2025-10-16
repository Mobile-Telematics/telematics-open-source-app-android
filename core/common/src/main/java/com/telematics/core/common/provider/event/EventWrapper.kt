package com.telematics.core.common.provider.event

/**
 * A wrapper class to ensure that event is consumed only once.
 * Intended to be used with framework's LiveData<T> or with Kotlin's Channel<T>.
 */
class EventWrapper<E> private constructor(
    private var consumed: Boolean,
    val eventData: E
) {

    constructor(data: E) : this(false, data)

    fun consumeEvent(consumer: (E) -> Unit) {
        if (!consumed) {
            consumed = true
            consumer(eventData)
        }
    }
}
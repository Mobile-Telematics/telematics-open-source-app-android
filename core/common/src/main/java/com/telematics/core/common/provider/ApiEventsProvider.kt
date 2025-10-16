package com.telematics.core.common.provider

import com.telematics.core.common.provider.event.EventFlow
import com.telematics.core.common.provider.event.MutableEventFlow
import com.telematics.core.common.provider.event.postEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiEventsImpl @Inject constructor(

) : ApiEventsProvider, ApiEventsControl {

    private val internal401ErrorEvent = MutableEventFlow<Unit>()
    override val unauthenticatedErrorEvent: EventFlow<Unit>
        get() = internal401ErrorEvent.asFlow


    private val internal410ErrorEvent = MutableEventFlow<Unit>()
    override val unsupportedVersionErrorEvent: EventFlow<Unit>
        get() = internal410ErrorEvent.asFlow


    override fun postUnauthenticatedErrorEvent() {
        internal401ErrorEvent.postEvent()
    }

    override fun postUnsupportedVersionErrorEvent() {
        internal410ErrorEvent.postEvent()
    }
}

interface ApiEventsProvider {
    val unauthenticatedErrorEvent: EventFlow<Unit>
    val unsupportedVersionErrorEvent: EventFlow<Unit>
}

interface ApiEventsControl {
    fun postUnauthenticatedErrorEvent()
    fun postUnsupportedVersionErrorEvent()
}
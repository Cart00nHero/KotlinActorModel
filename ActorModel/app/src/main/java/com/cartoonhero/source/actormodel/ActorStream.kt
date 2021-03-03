package com.cartoonhero.source.actormodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

interface Message

@ExperimentalCoroutinesApi
class ActorStream {
    private val streamScope: CoroutineScope =
        CoroutineScope(EmptyCoroutineContext + SupervisorJob())
    private val channel: BroadcastChannel<Message> = BroadcastChannel(100)

    fun send(event: Message) {
        streamScope.launch {
            channel.send(event)
        }
    }

    val messages: Flow<Message>
        get() = flow { emitAll(channel.openSubscription()) }
}
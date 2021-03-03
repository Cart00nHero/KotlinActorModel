package com.cartoonhero.source.actormodel

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.collect

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
abstract class Actor {

    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private val stream = ActorStream()
    private data class ActorMessage(
        val send: () -> Unit
    ): Message

    init {
        start()
    }

    private fun start() = scope.launch {
        val actor = actor<Message>(scope.coroutineContext) {
            for (msg in channel) {
                act(msg)
            }
        }
        stream.messages.collect(actor::send)
    }
    private fun sendMessage(message: Message) = stream.send(message)
    private fun act(message: Message) {
        when(message) {
            is ActorMessage -> message.send()
        }
    }

    fun send(sender: () -> Unit) {
        sendMessage(ActorMessage(sender))
    }
    fun cancel() {
        scope.cancel()
    }
}
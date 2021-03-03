package com.cartoonhero.source.actormodel

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.collect

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
abstract class Actor {

    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private val mSystem = ActorSystem()
    private var isStarted = true
    private data class ActorMessage(
        val send: () -> Unit
    ): Message

    init {
        startScope()
    }

    private fun startScope() = scope.launch {
        val actor = actor<Message>(scope.coroutineContext) {
            for (msg in channel) {
                act(msg)
            }
        }
        mSystem.messages.collect(actor::send)
    }
    private fun sendMessage(message: Message) = mSystem.send(message)
    private fun act(message: Message) {
        when(message) {
            is ActorMessage -> message.send()
        }
    }

    fun start() {
        if (!isStarted) startScope()
        isStarted = true
    }
    fun send(sender: () -> Unit) {
        sendMessage(ActorMessage(sender))
    }
    fun cancel() {
        if (isStarted) scope.cancel()
        isStarted = false
    }
}
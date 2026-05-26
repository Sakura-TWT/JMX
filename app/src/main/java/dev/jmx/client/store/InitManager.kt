package dev.jmx.client.store

import kotlinx.coroutines.CompletableDeferred

class InitManager {
    val deferred = CompletableDeferred<String>()
}
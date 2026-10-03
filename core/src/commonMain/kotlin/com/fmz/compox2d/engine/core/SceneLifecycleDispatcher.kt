package com.fmz.compox2d.engine.core

import kotlin.reflect.KClass

class SceneLifecycleDispatcher {

    private data class Handler(
        val onAdded: (GameObject) -> Unit,
        val onRemoved: (GameObject) -> Unit,
    )

    private val handlersByType = linkedMapOf<KClass<*>, Handler>()

    fun register(
        key: KClass<*>,
        onAdded: (GameObject) -> Unit,
        onRemoved: (GameObject) -> Unit,
    ) {
        handlersByType[key] = Handler(onAdded, onRemoved)
    }

    fun notifyAdded(gameObject: GameObject) {
        handlersByType.keys.filter { type ->
            type.isInstance(gameObject)
        }.forEach { key ->
            handlersByType[key]?.onAdded(gameObject)
        }
    }

    fun notifyRemoved(gameObject: GameObject) {
        handlersByType.keys.filter { type ->
            type.isInstance(gameObject)
        }.forEach { key ->
            handlersByType[key]?.onRemoved(gameObject)
        }
    }
}

package com.fmz.compox2d.engine.core

import kotlin.reflect.KClass

class SceneFixedStepDispatcher {

    private val actionsByType = linkedMapOf<KClass<*>, (Float) -> Unit>()

    fun register(key: KClass<*>, action: (Float) -> Unit) {
        actionsByType[key] = action
    }

    fun dispatch(dt: Float) {
        actionsByType.values.forEach { action -> action(dt) }
    }
}

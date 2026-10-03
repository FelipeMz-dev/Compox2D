package com.fmz.compox2d.engine.input

import com.fmz.compox2d.engine.core.GameObject
import kotlin.reflect.KClass
import kotlin.reflect.cast

internal class ListenerBinding<T : Any>(
    private val listenerClass: KClass<T>,
    private val onRegister: (T) -> Unit,
    private val onUnregister: (T) -> Unit
) : InstanceBinding {

    override fun register(instance: GameObject) {
        if (listenerClass.isInstance(instance)) {
            onRegister(listenerClass.cast(instance))
        }
    }

    override fun unregister(instance: GameObject) {
        if (listenerClass.isInstance(instance)) {
            onUnregister(listenerClass.cast(instance))
        }
    }
}

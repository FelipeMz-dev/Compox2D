package com.fmz.compox2d.engine.input

import com.fmz.compox2d.engine.core.GameObject

internal interface InstanceBinding {
    fun register(instance: GameObject)
    fun unregister(instance: GameObject)
}

package com.fmz.compox2d.physics

import com.fmz.compox2d.engine.core.GameObject
import org.jbox2d.dynamics.Body

sealed class PhysicsContactSource {
    abstract val owner: GameObject
    internal abstract val body: Body
}
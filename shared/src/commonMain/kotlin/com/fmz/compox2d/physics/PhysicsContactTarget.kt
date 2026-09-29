package com.fmz.compox2d.physics

import com.fmz.compox2d.engine.core.GameObject

data class PhysicsContactTarget(
    val id: Int,
    val owner: GameObject,
    val source: PhysicsContactSource,
    val layer: Int,
    val mask: Int,
) {
    fun canNotify(other: PhysicsContactTarget): Boolean {
        return (mask and other.layer) != 0 && (other.mask and layer) != 0
    }
}
package com.fmz.compox2d.physics

fun interface CollisionListener {
    fun onCollision(event: CollisionEvent)
}

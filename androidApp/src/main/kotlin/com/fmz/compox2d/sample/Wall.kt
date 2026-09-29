package com.fmz.compox2d.sample

import com.fmz.compox2d.engine.core.GameObject
import com.fmz.compox2d.engine.core.TransformState
import com.fmz.compox2d.engine.graphics.GpuColor
import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.render.Renderer
import com.fmz.compox2d.physics.CollisionBodyType
import com.fmz.compox2d.physics.PhysicsMaterial
import com.fmz.compox2d.physics.Shape

class Wall(val pos: Vec2, val size: Vec2) : GameObject() {
    override fun onEnterScene() {
        updatePosition { pos }
        createRigidBody(
            shape = Shape.BoxShape(size),
            type = CollisionBodyType.Static,
            material = PhysicsMaterial(density = 1f, friction = 0f, restitution = 1f)
        )
    }

    override fun Renderer.onRender(state: TransformState) {
        drawRect(size = size, color = GpuColor.Companion.Gray, state = state)
    }
}
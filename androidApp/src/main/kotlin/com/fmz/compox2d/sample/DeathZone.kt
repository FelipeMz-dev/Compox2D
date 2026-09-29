package com.fmz.compox2d.sample

import com.fmz.compox2d.engine.core.GameObject
import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.physics.Shape
import com.fmz.compox2d.physics.TriggerEvent
import com.fmz.compox2d.physics.TriggerListener

class DeathZone(val scene: SceneEventListener, val pos: Vec2, val size: Vec2)
    : GameObject(), TriggerListener {
    override fun onEnterScene() {
        updatePosition { pos }
        createTrigger(shape = Shape.BoxShape(size))
    }

    override fun onTrigger(event: TriggerEvent) {
        (event.other.owner as? Ball)?.run {
            scene.onBallLost()
        }
    }
}
package com.fmz.compox2d.engine.input.touch

import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.math.clamp
import com.fmz.compox2d.engine.math.div
import com.fmz.compox2d.engine.math.minus

class VirtualAxis(
    private val id: String,
    private val center: Vec2,
    private val radius: Float,
    private val input: com.fmz.compox2d.engine.input.touch.TouchManager
) {
    fun onDrag(pos: Vec2) {
        val delta = (pos - center).clamp(radius)
        input.dispatch(
            _root_ide_package_.com.fmz.compox2d.engine.input.touch.TouchEvent.AxisEvent(
                id = id,
                value = delta / radius
            )
        )
    }
}
package com.fmz.compox2d.engine.input.mouse

import com.fmz.compox2d.engine.math.Vec2

sealed interface MouseEvent {

    data class MouseMoveCursorEvent(
        val position: Vec2
    ) : MouseEvent

    data class MouseClickEvent(
        val position: Vec2,
        val button: com.fmz.compox2d.engine.input.MouseButton
    ) : MouseEvent

    data class MouseReleaseEvent(
        val position: Vec2,
        val button: com.fmz.compox2d.engine.input.MouseButton
    ) : MouseEvent

    data class MouseScrollEvent(
        val position: Vec2,
        val scrollX: Float,
        val scrollY: Float
    ) : MouseEvent

    data class MouseDragEvent(
        val start: Vec2,
        val current: Vec2,
        val delta: Vec2,
        val button: com.fmz.compox2d.engine.input.MouseButton
    ) : MouseEvent
}
package com.fmz.compox2d.engine.input.touch

import com.fmz.compox2d.engine.math.Vec2

sealed interface TouchEvent {

    data class PressEvent(
        val position: Vec2
    ) : TouchEvent

    data class TapEvent(
        val position: Vec2
    ) : TouchEvent

    data class HeldPressedEvent(
        val position: Vec2,
        val dt: Float
    ) : TouchEvent

    data class DragEvent(
        val start: Vec2,
        val current: Vec2,
    ) : TouchEvent

    object StopDragEvent : TouchEvent

    data class AxisEvent(
        val id: String,
        val value: Vec2
    ) : TouchEvent

    data class ButtonEvent(
        val id: String,
        val pressed: Boolean
    ) : TouchEvent
}
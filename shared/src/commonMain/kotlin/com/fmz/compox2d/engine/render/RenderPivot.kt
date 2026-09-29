package com.fmz.compox2d.engine.render

sealed class RenderPivot {
    object Center : RenderPivot()
    object Top : RenderPivot()
    object Bottom : RenderPivot()
    object Left : RenderPivot()
    object Right : RenderPivot()
    object TopLeft : RenderPivot()
    object TopRight : RenderPivot()
    object BottomLeft : RenderPivot()
    object BottomRight : RenderPivot()
    data class Custom(val x: Float, val y: Float) : RenderPivot()
}
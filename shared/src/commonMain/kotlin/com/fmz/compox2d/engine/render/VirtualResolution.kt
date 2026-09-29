package com.fmz.compox2d.engine.render

import com.fmz.compox2d.engine.math.Vec2

/**
 * Resoluciones virtuales predefinidas para diferentes aspectos de pantalla.
 * Agnóstico de plataforma.
 */
sealed class VirtualResolution(val width: Float, val height: Float) {
    object Portrait : VirtualResolution(360f, 640f)
    object Landscape : VirtualResolution(480f, 270f)
    object LandscapeHD : VirtualResolution(720f, 480f)
    object LandscapeFHD : VirtualResolution(1080f, 720f)
    object Square : VirtualResolution(640f, 640f)
    object PixelArt : VirtualResolution(320f, 180f)
    object Undefined : VirtualResolution(0f, 0f)
    data class Custom(val size: Vec2) : VirtualResolution(size.x, size.y)
    fun aspectRatio() = if (height != 0f) width / height else 0f
}

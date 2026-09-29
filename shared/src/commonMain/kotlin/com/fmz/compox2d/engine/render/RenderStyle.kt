package com.fmz.compox2d.engine.render

/**
 * Estilos de render agnósticos de plataforma.
 */
sealed class RenderStyle {
    object Fill : RenderStyle()
    data class Stroke(val width: Float) : RenderStyle()
}
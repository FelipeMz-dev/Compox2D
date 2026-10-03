package com.fmz.compox2d.engine.render

import com.fmz.compox2d.engine.graphics.GpuColor

/**
 * Estilo de texto agnóstico de plataforma.
 */
data class RenderTextStyle(
    val fontSize: Float = 16f,
    val fontSizeUnit: RenderFontSize = RenderFontSize.Sp,
    val color: GpuColor = GpuColor.Black,
) {
    companion object {
        fun default() = RenderTextStyle()
        fun withColor(color: GpuColor) = default().copy(color = color)
        fun withFontSize(size: Float) = default().copy(fontSize = size)
        fun withFontSize(size: Float, unit: RenderFontSize) = default().copy(fontSize = size, fontSizeUnit = unit)
    }
}
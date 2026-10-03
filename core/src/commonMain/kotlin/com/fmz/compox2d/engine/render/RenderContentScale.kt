package com.fmz.compox2d.engine.render

/**
 * Modos de escala de contenido agnósticos de plataforma.
 */
enum class RenderContentScale {
    Fit,
    Crop,
    FillBounds,
    FillHeight,
    FillWidth,
    Inside,
    None,
}
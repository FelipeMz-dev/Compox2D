package com.fmz.compox2d.engine.render

import androidx.compose.ui.graphics.drawscope.DrawScope

internal fun interface RenderDrawer {
    fun DrawScope.draw()
}

internal class RenderCommand(
    val order: Int,
    val drawer: RenderDrawer
)
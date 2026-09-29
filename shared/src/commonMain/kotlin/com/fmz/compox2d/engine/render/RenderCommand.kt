package com.fmz.compox2d.engine.render

import com.fmz.compox2d.compose.RenderDrawer

internal class RenderCommand(
    val order: Int,
    val drawer: RenderDrawer
)
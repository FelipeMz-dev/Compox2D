package com.fmz.compox2d.engine.graphics

/**
 * Representación agnóstica de plataforma para rectángulos.
 */
data class GpuRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    val width: Float
        get() = right - left

    val height: Float
        get() = bottom - top

    val size: com.fmz.compox2d.engine.graphics.GpuSize
        get() = _root_ide_package_.com.fmz.compox2d.engine.graphics.GpuSize(width, height)

    val topLeft: com.fmz.compox2d.engine.graphics.GpuOffset
        get() = _root_ide_package_.com.fmz.compox2d.engine.graphics.GpuOffset(left, top)

    val bottomRight: com.fmz.compox2d.engine.graphics.GpuOffset
        get() = _root_ide_package_.com.fmz.compox2d.engine.graphics.GpuOffset(right, bottom)

    val center: com.fmz.compox2d.engine.graphics.GpuOffset
        get() = _root_ide_package_.com.fmz.compox2d.engine.graphics.GpuOffset(
            (left + right) / 2,
            (top + bottom) / 2
        )

    companion object {
        fun fromLTWH(left: Float, top: Float, width: Float, height: Float): com.fmz.compox2d.engine.graphics.GpuRect =
            _root_ide_package_.com.fmz.compox2d.engine.graphics.GpuRect(
                left,
                top,
                left + width,
                top + height
            )

        fun fromCenter(center: com.fmz.compox2d.engine.graphics.GpuOffset, width: Float, height: Float): com.fmz.compox2d.engine.graphics.GpuRect {
            val halfWidth = width / 2
            val halfHeight = height / 2
            return _root_ide_package_.com.fmz.compox2d.engine.graphics.GpuRect(
                center.x - halfWidth,
                center.y - halfHeight,
                center.x + halfWidth,
                center.y + halfHeight
            )
        }
    }

    fun contains(offset: com.fmz.compox2d.engine.graphics.GpuOffset): Boolean =
        offset.x >= left && offset.x <= right && offset.y >= top && offset.y <= bottom

    fun overlaps(other: com.fmz.compox2d.engine.graphics.GpuRect): Boolean =
        left < other.right && right > other.left && top < other.bottom && bottom > other.top

    fun translate(dx: Float, dy: Float): com.fmz.compox2d.engine.graphics.GpuRect =
        _root_ide_package_.com.fmz.compox2d.engine.graphics.GpuRect(
            left + dx,
            top + dy,
            right + dx,
            bottom + dy
        )

    fun inflate(dx: Float, dy: Float): com.fmz.compox2d.engine.graphics.GpuRect =
        _root_ide_package_.com.fmz.compox2d.engine.graphics.GpuRect(
            left - dx,
            top - dy,
            right + dx,
            bottom + dy
        )
}


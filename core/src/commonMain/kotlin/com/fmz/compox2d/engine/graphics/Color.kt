package com.fmz.compox2d.engine.graphics

import kotlin.math.roundToInt

enum class GradientDirection {
    Horizontal,
    Vertical
}

data class GradientStop(
    val color: GpuColor,
    val stop: Float
) {
    init {
        require(stop in 0f..1f) { "stop debe estar entre 0 y 1" }
        require(!color.isGradient) { "un stop no puede ser un gradiente" }
    }
}

/**
 * Representación agnóstica de plataforma para colores.
 * Los valores están en rango [0.0, 1.0] para compatibilidad universal.
 * Además soporta gradientes con lista de stops y dirección horizontal/vertical.
 */
data class GpuColor(
    val red: Float,
    val green: Float,
    val blue: Float,
    val alpha: Float = 1f,
    val gradientStops: List<GradientStop> = emptyList(),
    val gradientDirection: GradientDirection = GradientDirection.Horizontal
) {
    init {
        require(red in 0f..1f) { "red debe estar entre 0 y 1" }
        require(green in 0f..1f) { "green debe estar entre 0 y 1" }
        require(blue in 0f..1f) { "blue debe estar entre 0 y 1" }
        require(alpha in 0f..1f) { "alpha debe estar entre 0 y 1" }
        require(gradientStops.size <= 1 || gradientStops.zipWithNext().all { (current, next) -> current.stop <= next.stop }) {
            "los stops del gradiente deben estar ordenados ascendentemente"
        }
    }

    val isGradient: Boolean
        get() = gradientStops.isNotEmpty()

    /**
     * Convierte a formato ARGB de 32 bits para Android.
     */
    fun toArgb(): Int {
        val a = (alpha * 255).roundToInt()
        val r = (red * 255).roundToInt()
        val g = (green * 255).roundToInt()
        val b = (blue * 255).roundToInt()
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }

    /**
     * Crea una copia con alfa modificado.
     */
    fun withAlpha(alpha: Float): GpuColor = GpuColor(
        red = red,
        green = green,
        blue = blue,
        alpha = alpha,
        gradientStops = gradientStops,
        gradientDirection = gradientDirection
    )

    companion object {
        val White = GpuColor(1f, 1f, 1f, 1f)
        val Black = GpuColor(0f, 0f, 0f, 1f)
        val Red = GpuColor(1f, 0f, 0f, 1f)
        val Green = GpuColor(0f, 1f, 0f, 1f)
        val Blue = GpuColor(0f, 0f, 1f, 1f)
        val Gray = GpuColor(0.5f, 0.5f, 0.5f, 1f)
        val Transparent = GpuColor(0f, 0f, 0f, 0f)
        val Yellow = GpuColor(1f, 1f, 0f, 1f)
        val Cyan = GpuColor(0f, 1f, 1f, 1f)
        val Magenta = GpuColor(1f, 0f, 1f, 1f)

        fun gradient(
            vararg colors: GpuColor,
            direction: GradientDirection = GradientDirection.Horizontal
        ): GpuColor {
            require(colors.isNotEmpty()) { "se necesita al menos un color para el gradiente" }
            if (colors.size == 1) {
                return GpuColor(
                    red = colors[0].red,
                    green = colors[0].green,
                    blue = colors[0].blue,
                    alpha = colors[0].alpha,
                    gradientStops = listOf(GradientStop(colors[0], 0f), GradientStop(colors[0], 1f)),
                    gradientDirection = direction
                )
            }

            val stops = colors.mapIndexed { index, color ->
                GradientStop(color, index.toFloat() / (colors.size - 1).coerceAtLeast(1))
            }
            return GpuColor(
                red = colors.first().red,
                green = colors.first().green,
                blue = colors.first().blue,
                alpha = colors.first().alpha,
                gradientStops = stops,
                gradientDirection = direction
            )
        }

        fun gradient(
            stops: List<GradientStop>,
            direction: GradientDirection = GradientDirection.Horizontal
        ): GpuColor {
            require(stops.isNotEmpty()) { "se necesita al menos un stop para el gradiente" }
            val ordered = stops.sortedBy { it.stop }
            return GpuColor(
                red = ordered.first().color.red,
                green = ordered.first().color.green,
                blue = ordered.first().color.blue,
                alpha = ordered.first().color.alpha,
                gradientStops = ordered,
                gradientDirection = direction
            )
        }

        /**
         * Crear color desde ARGB de 32 bits.
         */
        fun fromArgb(argb: Int): GpuColor {
            val a = ((argb shr 24) and 0xff) / 255f
            val r = ((argb shr 16) and 0xff) / 255f
            val g = ((argb shr 8) and 0xff) / 255f
            val b = (argb and 0xff) / 255f
            return GpuColor(r, g, b, a)
        }

        /**
         * Crear color desde RGB en rango [0-255].
         */
        fun fromRgb(r: Int, g: Int, b: Int, a: Int = 255): GpuColor {
            return GpuColor(
                r / 255f,
                g / 255f,
                b / 255f,
                a / 255f
            )
        }
    }
}



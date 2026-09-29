package com.fmz.compox2d.engine.math

import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random

fun Float.round(decimals: Int): Float {
    val factor = 10f.pow(decimals)
    return try {
        (this * factor).roundToInt() / factor
    } catch (e: Exception){
        0f
    }
}

fun Float.degToRad() = this * PI / 180.00

fun Float.radToDeg() = (this * 180 / PI).toFloat()

fun ClosedRange<Float>.random() = start + (endInclusive - start) * Random.nextFloat()
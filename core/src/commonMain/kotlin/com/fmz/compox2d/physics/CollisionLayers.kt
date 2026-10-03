package com.fmz.compox2d.physics

object CollisionLayers {
    const val Default: Int = 1 shl 0
    const val World: Int = 1 shl 1
    const val Player: Int = 1 shl 2
    const val Enemy: Int = 1 shl 3
    const val Trigger: Int = 1 shl 4

    @Deprecated("Renamed to Trigger", ReplaceWith("Trigger"))
    const val Sensor: Int = Trigger

    const val All: Int = Int.MAX_VALUE
}

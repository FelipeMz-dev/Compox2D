package com.fmz.compox2d.engine.input.sensor

/**
 * Interfaz agnóstica de plataforma para entrada de sensores.
 */
interface SensorSystem {

    /**
     * Inicia la detección de sensores.
     */
    fun start()

    /**
     * Detiene la detección de sensores.
     */
    fun stop()
}

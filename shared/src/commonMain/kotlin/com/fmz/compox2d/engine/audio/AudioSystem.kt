package com.fmz.compox2d.engine.audio

import com.fmz.compox2d.engine.core.AudioId

/**
 * Interfaz agnóstica de plataforma para gestión de audio.
 * Las implementaciones específicas de plataforma manejan los detalles.
 */
interface AudioSystem {

    fun loadSound(id: AudioId, resourceId: Any)

    fun loadMusic(id: AudioId, resourceId: Any)
}

package com.fmz.compox2d.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import com.fmz.compox2d.engine.assets.SpriteManager
import com.fmz.compox2d.engine.audio.AudioSystem
import com.fmz.compox2d.engine.core.GameScene
import com.fmz.compox2d.engine.core.SceneDependencies
import com.fmz.compox2d.engine.input.GameInput
import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.render.Viewport
import com.fmz.compox2d.engine.render.VirtualResolution
import com.fmz.compox2d.engine.time.GameLoop

@Composable
fun GameSceneView(
    modifier: Modifier = Modifier,
    scene: GameScene,
    spriteManager: SpriteManager = rememberSpriteManager(),
    audioManager: AudioSystem = rememberAudioSystem(),
    gameInput: GameInput = rememberGameInput(),
    contentScale: ContentScale = ContentScale.Crop,
    virtualResolution: VirtualResolution = VirtualResolution.Undefined,
) {
    val density = LocalDensity.current
    val loop = remember { GameLoop(scene) }
    val frameTicker = remember { mutableIntStateOf(0) }
    var resolution by remember { mutableStateOf(virtualResolution) }
    var scale by remember { mutableStateOf(ScaleFactor.Unspecified) }
    val textMeasurer = rememberTextMeasurer()
    val focusRequester = remember { FocusRequester() }
    val sensorInputAdapter = gameInput.sensorProcessor?.let { rememberSensorSystem(it) }
    val renderer = remember(scene, spriteManager, textMeasurer, density) {
        RendererImpl(
            spriteManager,
            textMeasurer,
            scene.camera2D,
            density
        )
    }

    LaunchedEffect(Unit) {
        gameInput.keyboardProcessor?.apply { focusRequester.requestFocus() }
        scene.attach(
            SceneDependencies(
                spriteManager = spriteManager,
                audioManager = audioManager,
                gameInput = gameInput
            )
        )
        scene.startScene()
        while (true) {
            withFrameNanos { frameTime ->
                loop.onFrame(frameTime)
                frameTicker.intValue++
            }
        }
    }


    fun DrawScope.render() {
        clipRect(right = resolution.width, bottom = resolution.height) {
            renderer.bind(this)
            scene.apply { render(renderer, loop.alpha()) }
        }
    }

    fun updateScreenSize(size: IntSize) {
        if (virtualResolution is VirtualResolution.Undefined) {
            resolution = VirtualResolution.Custom(size.toSize().toVec2())
        }
        scale = contentScale.computeScaleFactor(
            resolution.toSizeCompose(),
            size.toSize()
        )
        val viewport = Viewport(
            size = Vec2(resolution.width, resolution.height),
            scale = Vec2(scale.scaleX, scale.scaleY)
        )
        scene.updateViewport(viewport)
    }

    Canvas(
        modifier = modifier
            .onSizeChanged { updateScreenSize(it) }
            .runInputProcessors(gameInput, focusRequester)
    ) {
        if (virtualResolution !is VirtualResolution.Undefined) {
            inset(scale.scaleX, scale.scaleY) {
                withTransform(
                    transformBlock = {
                        scale(scale.scaleX, scale.scaleY, Offset.Zero)
                    }
                ) { render() }
            }
        } else render()
        frameTicker.intValue
    }

    DisposableEffect(Unit) {
        sensorInputAdapter?.start()
        onDispose {
            sensorInputAdapter?.stop()
        }
    }
}
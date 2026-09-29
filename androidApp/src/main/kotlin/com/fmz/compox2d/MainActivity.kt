package com.fmz.compox2d

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.fmz.compox2d.compose.GameSceneView
import com.fmz.compox2d.sample.MainScene

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            val scene = remember { MainScene() }
            Scaffold {
                GameSceneView(
                    Modifier
                        .fillMaxSize()
                        .padding(it),
                    scene
                )
            }
        }
    }
}


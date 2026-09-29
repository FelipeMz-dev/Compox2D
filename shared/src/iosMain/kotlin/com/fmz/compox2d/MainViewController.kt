package com.fmz.compox2d

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController(content: @Composable () -> Unit) = ComposeUIViewController {
    content()
}
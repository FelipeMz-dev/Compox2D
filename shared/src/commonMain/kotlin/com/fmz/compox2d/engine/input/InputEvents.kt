package com.fmz.compox2d.engine.input

/**
 * Evento de teclado agnóstico de plataforma.
 */

enum class KeyButton {
    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,
    Num0, Num1, Num2, Num3, Num4, Num5, Num6, Num7, Num8, Num9,
    Space, Enter, Escape, Backspace, Tab, Shift, Control, Alt,
    ArrowUp, ArrowDown, ArrowLeft, ArrowRight,
    // Agregar más teclas según sea necesario
}

/**
 * Botón de mouse agnóstico de plataforma.
 */
enum class MouseButton {
    Left, Right, Middle, Unknown
}

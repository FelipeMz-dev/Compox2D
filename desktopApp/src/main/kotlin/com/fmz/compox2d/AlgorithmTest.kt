package com.fmz.compox2d

fun main() {
    var n = 1
    var contador = 0
    while (n <= 100) {
         if (n % 3 == 0 || n % 5 == 0) contador++
        n++
    }
    println(contador)
}
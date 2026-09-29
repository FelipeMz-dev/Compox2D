package com.fmz.compox2d

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
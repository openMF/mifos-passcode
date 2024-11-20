package com.mifos.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
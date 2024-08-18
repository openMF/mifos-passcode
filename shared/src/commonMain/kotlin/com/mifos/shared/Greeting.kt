package com.mifos.shared

import com.mifos.shared.Platform
import com.mifos.shared.getPlatform

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}
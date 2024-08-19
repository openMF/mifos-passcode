package com.mifos.shared

class AndroidPlatform : Platform {
//    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
    override val name: String = "Android"
}

actual fun getPlatform(): Platform = AndroidPlatform()
package com.mifos.shared

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
//    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val name: String = "Ios"
}

actual fun getPlatform(): Platform = IOSPlatform()
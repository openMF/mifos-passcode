package com.mifos.passcode

import android.app.Application
import com.mifos.mobile.passcode.utils.ForegroundChecker.Companion.init
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by dilpreet on 19/01/18.
 */

@HiltAndroidApp
class MifosApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //need to initialize this
        init(this)
    }
}
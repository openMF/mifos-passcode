package com.mifos.mobile.passcode

import androidx.appcompat.app.AppCompatActivity
import com.mifos.mobile.passcode.MifosPassCodeActivity.Companion.startMifosPassCodeActivity
import com.mifos.mobile.passcode.utils.ForegroundChecker

/**
 * Created by dilpreet on 19/01/18.
 */
abstract class BasePassCodeActivity : AppCompatActivity(), ForegroundChecker.Listener {
    override fun onResume() {
        super.onResume()
        ForegroundChecker.get()?.addListener(this)
        ForegroundChecker.get()?.onActivityResumed()
    }

    override fun onPause() {
        super.onPause()
        ForegroundChecker.get()?.onActivityPaused()
    }

    override fun onBecameForeground() {
        startMifosPassCodeActivity(
            this, passCodeClass,
            false
        )
    }

    abstract val passCodeClass: Class<*>?
}
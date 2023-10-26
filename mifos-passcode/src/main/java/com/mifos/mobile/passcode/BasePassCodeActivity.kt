package com.mifos.mobile.passcode

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.mifos.mobile.passcode.utils.ForegroundChecker
import com.mifos.mobile.passcode.utils.ForegroundChecker.Companion.get

/**
 * Created by dilpreet on 19/01/18.
 */
abstract class BasePassCodeActivity : ComponentActivity(), ForegroundChecker.Listener {
    override fun onResume() {
        super.onResume()
        get()!!.addListener(this)
        get()!!.onActivityResumed()
    }

    override fun onPause() {
        super.onPause()
        get()!!.onActivityPaused()
    }

    override fun onBecameForeground() {
        MifosPassCodeActivity.startMifosPassCodeActivity(
            this, passCodeClass,
            false
        )
    }

    abstract val passCodeClass: Class<*>?
}
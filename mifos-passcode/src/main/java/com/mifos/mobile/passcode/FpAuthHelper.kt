package com.mifos.mobile.passcode

import android.content.Context
import android.os.Handler
import android.widget.Toast
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal

class FpAuthHelper(private val context: Context, private val fpAuthCallback: FpAuthCallback,
                   private val fpAuthDialog: FpAuthDialog) {
    private var cancellationSignal: CancellationSignal? = null
    private var handler: Handler = Handler()

    companion object {
        const val AUTH_FAILED_DELAY: Long = 1000
        const val AUTH_SUCCESS_DELAY: Long = 500
    }

    private val startScanning = Runnable {
        fpAuthDialog.run {
            setStatusText(context.getString(R.string.touch_the_sensor))
            setStatusIcon(R.drawable.ic_fingerprint_blue_48dp)
        }
    }

    fun startFpAuth() {
        if (!FpAuthSupport.checkAvailabiltyAndIfFingerprintRegistered(context)) {
            return
        }
        if (cancellationSignal == null) {
            cancellationSignal = CancellationSignal()
        }

//        val fingerprintManager = FingerprintManagerCompat.from(context)
//
//        fingerprintManager.authenticate(
//                null, 0, cancellationSignal,
//                object : FingerprintManagerCompat.AuthenticationCallback() {
//
//                    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
//                        super.onAuthenticationHelp(helpMsgId, helpString)
//                        Toast.makeText(context, helpString, Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onAuthenticationFailed() {
//                        super.onAuthenticationFailed()
//                        fpAuthDialog.run {
//                            setStatusIcon(R.drawable.ic_cancel_red_48dp)
//                            setStatusText(context.getString(R.string.finger_print_not_recognized))
//                        }
//                        handler.postDelayed(startScanning, AUTH_FAILED_DELAY)
//                    }
//
//                    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
//                        super.onAuthenticationSucceeded(result)
//                        fpAuthDialog.run {
//                            setStatusIcon(R.drawable.ic_check_circle_green_48dp)
//                            setStatusText(context.getString(R.string.authentication_successful))
//                        }
//                        handler.postDelayed({
//                            fpAuthDialog.dismiss()
//                            fpAuthCallback.onFpAuthSuccess()
//                        }, AUTH_SUCCESS_DELAY)
//                    }
//                }, null
//        )
    }

    fun stopFpAuth() {
        cancellationSignal?.run {
            cancel()
            cancellationSignal = null
        }
    }
}
package com.mifos.mobile.passcode

import android.content.Context
import android.os.Build
import androidx.core.hardware.fingerprint.FingerprintManagerCompat

object FpAuthSupport {

    @JvmStatic
    fun checkAvailability(context: Context): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                FingerprintManagerCompat.from(context).isHardwareDetected
    }

    @JvmStatic
    fun isFingerprintRegistered(context: Context): Boolean {
        return FingerprintManagerCompat.from(context).hasEnrolledFingerprints()
    }

    @JvmStatic
    fun checkAvailabiltyAndIfFingerprintRegistered(context: Context): Boolean {
        val fingerprintManagerCompat = FingerprintManagerCompat.from(context)
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                fingerprintManagerCompat.isHardwareDetected &&
                fingerprintManagerCompat.hasEnrolledFingerprints()
    }
}
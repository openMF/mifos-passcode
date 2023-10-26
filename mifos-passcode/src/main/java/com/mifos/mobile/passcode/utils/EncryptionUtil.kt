package com.mifos.mobile.passcode.utils

import android.util.Log
import androidx.annotation.IntDef

object EncryptionUtil {

    const val DEFAULT = 1
    const val MOBILE_BANKING = 2
    const val ANDROID_CLIENT = 3
    const val FINERACT_CN = 4


    @IntDef(DEFAULT, MOBILE_BANKING, ANDROID_CLIENT, FINERACT_CN)
    @Retention(AnnotationRetention.SOURCE)
    annotation class TYPE

    init {
        try {
            System.loadLibrary("encryption")
        } catch (e: UnsatisfiedLinkError) {
            Log.e("LoadJniLib", "Error: Could not load native library: ${e.message}")
        }
    }

    external fun getPassCodeHash(passcode: String): String

    fun getDefaultHash(passCode: String): String {
        return getPassCodeHash(passCode)
    }

    fun getMobileBankingHash(passCode: String): String {
        return getPassCodeHash(passCode)
    }

    fun getAndroidClientHash(passCode: String): String {
        return getPassCodeHash(passCode)
    }

    fun getFineractCNHash(passCode: String): String {
        return getPassCodeHash(passCode)
    }
}

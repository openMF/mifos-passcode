package com.mifos.mobile.passcode.utils

import android.util.Log
import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by dilpreet on 11/7/17.
 */
object EncryptionUtil {
    const val DEFAULT = 1
    const val MOBILE_BANKING = 2
    const val ANDROID_CLIENT = 3
    const val FINERACT_CN = 4
    private external fun getPassCodeHash(passcode: String): String

    @JvmStatic
    fun getDefaultHash(passCode: String): String {
        return getPassCodeHash(passCode)
    }

    @JvmStatic
    fun getMobileBankingHash(passCode: String): String {
        return getPassCodeHash(passCode)
    }

    @JvmStatic
    fun getAndroidClientHash(passCode: String): String {
        return getPassCodeHash(passCode)
    }

    @JvmStatic
    fun getFineractCNHash(passCode: String): String {
        return getPassCodeHash(passCode)
    }

    @IntDef(DEFAULT, MOBILE_BANKING, ANDROID_CLIENT, FINERACT_CN)
    @Retention(RetentionPolicy.SOURCE)
    annotation class TYPE

    init {
        try {
            System.loadLibrary("encryption")
        } catch (e: UnsatisfiedLinkError) {
            Log.e("LoadJniLib", "Error: Could not load native library: " + e.message)
        }
    }
}
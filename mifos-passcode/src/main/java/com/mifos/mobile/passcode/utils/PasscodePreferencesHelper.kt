package com.mifos.mobile.passcode.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Created by dilpreet on 19/01/18.
 */
class PasscodePreferencesHelper(context: Context?) {
    private val sharedPreferences: SharedPreferences
    private val TOKEN = "preferences_mifos_passcode_string"
    private val FINGERPRINTENABLER = "fingerprint_enable_dialog"
    private val AUTHTYPE = "auth_type"
    fun savePassCode(token: String?) {
        sharedPreferences.edit().putString(TOKEN, token).apply()
    }

    val passCode: String?
        get() = sharedPreferences.getString(TOKEN, "")
    var fingerprintEnableDialogState: Boolean
        get() = sharedPreferences.getBoolean(FINGERPRINTENABLER, true)
        set(show) {
            sharedPreferences.edit().putBoolean(FINGERPRINTENABLER, show).apply()
        }
    var authType: String?
        get() = sharedPreferences.getString(AUTHTYPE, "")
        set(authType) {
            sharedPreferences.edit().putString(AUTHTYPE, authType).apply()
        }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    init {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }
}
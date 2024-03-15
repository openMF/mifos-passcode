package com.mifos.compose.utility

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.mifos.compose.R

class PreferenceManager(context: Context) {
    private val sharedPreference = context.getSharedPreferences(
        R.string.pref_name.toString(),
        Context.MODE_PRIVATE
    )

    var isPasscodeWrong = mutableStateOf(false)

    var hasPasscode: Boolean
        get() = sharedPreference.getBoolean(R.string.has_passcode.toString(), false)
        set(value) = sharedPreference.edit().putBoolean(R.string.has_passcode.toString(), value)
            .apply()

    fun savePasscode(passcode: String) {
        sharedPreference.edit().putString(R.string.passcode.toString(), passcode).apply()
        hasPasscode = true
    }

    fun getSavedPasscode(): String {
        return sharedPreference.getString(R.string.passcode.toString(), "").toString()
    }

    fun clearPasscode() {
        sharedPreference.edit().clear().apply()
        hasPasscode = false
        isPasscodeWrong.value = false
    }
}
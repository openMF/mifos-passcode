package com.mifos.compose

import android.os.Build
import androidx.annotation.RequiresApi
import com.mifos.compose.utility.PreferenceManager
import javax.inject.Inject

class PasscodeRepositoryImpl @Inject constructor(private val preferenceManager: PreferenceManager) :
    PasscodeRepository {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getSavedPasscode(): String {
        return preferenceManager.getSavedPasscode()
    }

    override val hasPasscode: Boolean
        get() = preferenceManager.hasPasscode

    @RequiresApi(Build.VERSION_CODES.M)
    override fun savePasscode(passcode: String) {
        preferenceManager.savePasscode(passcode)
    }
}
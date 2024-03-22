package com.mifos.compose

import com.mifos.compose.utility.PreferenceManager
import javax.inject.Inject

class PasscodeRepositoryImpl @Inject constructor(private val preferenceManager: PreferenceManager) :
    PasscodeRepository {

    override fun getSavedPasscode(): String {
        return preferenceManager.getSavedPasscode()
    }

    override val hasPasscode: Boolean
        get() = preferenceManager.hasPasscode

    override fun savePasscode(passcode: String) {
        preferenceManager.savePasscode(passcode)
    }
}
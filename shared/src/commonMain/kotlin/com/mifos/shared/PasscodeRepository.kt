package com.mifos.shared

interface PasscodeRepository {
    fun getSavedPasscode(): String
    val hasPasscode: Boolean
    fun savePasscode(passcode: String)
}
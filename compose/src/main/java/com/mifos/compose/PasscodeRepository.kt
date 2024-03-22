package com.mifos.compose

interface PasscodeRepository {
    fun getSavedPasscode(): String
    val hasPasscode: Boolean
    fun savePasscode(passcode: String)
}
package com.mifos.compose.utility

interface PasscodeListener {
    fun onPassCodeReceive(passcode: String)
    fun onPasscodeReject()
    fun onPasscodeForgot()
    fun onPasscodeSkip()
}
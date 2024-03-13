package com.assignment.compose.utility

interface PasscodeListener {
    fun onPassCodeReceive(passcode: String)
    fun onPasscodeReject()
    fun onPasscodeForgot()
}
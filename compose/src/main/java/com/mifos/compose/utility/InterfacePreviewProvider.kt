package com.mifos.compose.utility

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class InterfacePreviewProvider : PreviewParameterProvider<PasscodeListener> {
    override val values: Sequence<PasscodeListener>
        get() = sequenceOf(object : PasscodeListener {

            override fun onPasscodeReject() {}

            override fun onPassCodeReceive(passcode: String) {}

            override fun onPasscodeForgot() {}

            override fun onPasscodeSkip() {}
        })
}
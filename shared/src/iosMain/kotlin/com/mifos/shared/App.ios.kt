package com.mifos.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.mifos.shared.utility.BioMetricUtil
import com.mifos.shared.component.PasscodeScreen
import com.mifos.shared.viewmodels.BiometricAuthorizationViewModel
import platform.UIKit.UIViewController

fun MainViewController(
    bioMetricUtil: BioMetricUtil,
    biometricViewModel: BiometricAuthorizationViewModel): UIViewController = ComposeUIViewController {
    PasscodeScreen(
        onPasscodeConfirm = {
        },
        onSkipButton = {
        },
        onForgotButton = {},
        onPasscodeRejected = {},
        bioMetricUtil = bioMetricUtil,
        biometricAuthorizationViewModel = biometricViewModel,
        onBiometricAuthSuccess = {
         },
        enableBiometric = true
    )
}
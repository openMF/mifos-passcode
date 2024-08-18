package com.mifos.passcode

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import com.mifos.shared.BiometricUtilAndroidImpl
import com.mifos.shared.CipherUtilAndroidImpl
import com.mifos.shared.utility.PreferenceManager
import com.mifos.shared.PasscodeRepository
import com.mifos.shared.PasscodeRepositoryImpl
import com.mifos.shared.viewmodels.BiometricAuthorizationViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mifos.shared.component.PasscodeScreen


class MainActivity : FragmentActivity() {
    private val bioMetricUtil by lazy {
        BiometricUtilAndroidImpl(this, CipherUtilAndroidImpl())
    }
    private lateinit var passcodeRepository: PasscodeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bioMetricUtil.preparePrompt(
            title= getString(R.string.biometric_auth_title),
            subtitle = "",
            description = getString(R.string.biometric_auth_description)
        )
        passcodeRepository = PasscodeRepositoryImpl(PreferenceManager())
        setContent {
            val biometricViewModel: BiometricAuthorizationViewModel = viewModel()

            PasscodeScreen(
                onForgotButton = { onPasscodeForgot() },
                onSkipButton = { onPasscodeSkip() },
                onPasscodeConfirm = { onPassCodeReceive(it) },
                onPasscodeRejected = { onPasscodeReject() },
                bioMetricUtil = bioMetricUtil,
                biometricAuthorizationViewModel = biometricViewModel,
                onBiometricAuthSuccess = { launchNextActivity() }
            )
        }
    }

    private fun onPassCodeReceive(passcode: String) {
        if (passcodeRepository.getSavedPasscode() == passcode) {
            launchNextActivity()
        }
    }

    private fun onPasscodeReject() {}

    private fun onPasscodeForgot() {
        // Add logic to redirect user to login page
    }

    private fun onPasscodeSkip() {
        finish()
    }

    private fun launchNextActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

//@Preview
//@Composable
//fun DefaultPreview() {
//    MyApplicationTheme {
//        GreetingView("Hello, Android!")
//    }
//}
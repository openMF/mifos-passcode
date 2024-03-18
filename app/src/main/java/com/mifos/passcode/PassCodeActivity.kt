package com.mifos.passcode

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.mifos.compose.viewmodels.PasscodeViewModel
import com.mifos.compose.component.PasscodeScreen
import com.mifos.compose.theme.MifosPasscodeTheme
import com.mifos.compose.utility.PreferenceManager

/**
 * Created by dilpreet on 19/01/18.
 */
class PassCodeActivity : AppCompatActivity() {

    private lateinit var passcodeViewModel: PasscodeViewModel
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(this)
        passcodeViewModel = PasscodeViewModel(preferenceManager)

        setContent {
            MifosPasscodeTheme {
                PasscodeScreen(
                    passcodeViewModel,
                    preferenceManager,
                    onForgotButton = { onPasscodeForgot() },
                    onSkipButton = { onPasscodeSkip() },
                    onPasscodeConfirm = { onPassCodeReceive(it) },
                    onPasscodeRejected = { onPasscodeReject() }
                )
            }
        }
    }

    private fun onPassCodeReceive(passcode: String) {
        if (preferenceManager.getSavedPasscode() == passcode) {
            startActivity(Intent(this, LoginActivity::class.java))
            Toast.makeText(this, "New Screen", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun onPasscodeReject() {}

    private fun onPasscodeForgot() {
        Toast.makeText(this, "Forgot Passcode", Toast.LENGTH_SHORT).show()
        // Add logic to redirect user to login page
    }

    private fun onPasscodeSkip() {
        Toast.makeText(this, "Skip Button", Toast.LENGTH_SHORT).show()
        finish()
    }
}
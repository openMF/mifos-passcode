package com.mifos.compose.component

import android.content.Intent
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.compose.BiometricPromptManager
import com.mifos.compose.PasscodeRepository
import com.mifos.compose.R
import com.mifos.compose.theme.blueTint
import com.mifos.compose.utility.Constants.PASSCODE_LENGTH
import com.mifos.compose.utility.PreferenceManager
import com.mifos.compose.utility.ShakeAnimation.performShakeAnimation
import com.mifos.compose.utility.VibrationFeedback.vibrateFeedback
import com.mifos.compose.viewmodels.PasscodeViewModel

/**
 * @author pratyush
 * @since 15/3/24
 */

@Composable
fun PasscodeScreen(
    viewModel: PasscodeViewModel = hiltViewModel(),
    onForgotButton: () -> Unit,
    onSkipButton: () -> Unit,
    onPasscodeConfirm: (String) -> Unit,
    onPasscodeRejected: () -> Unit,
    onBiometricAuthenticated: () -> Unit,
    activity: AppCompatActivity
) {
    val promptManager = BiometricPromptManager(activity)
    val context = LocalContext.current
    val preferenceManager = remember { PreferenceManager(context) }
    val activeStep by viewModel.activeStep.collectAsStateWithLifecycle()
    val filledDots by viewModel.filledDots.collectAsStateWithLifecycle()
    val passcodeVisible by viewModel.passcodeVisible.collectAsStateWithLifecycle()
    val currentPasscode by viewModel.currentPasscodeInput.collectAsStateWithLifecycle()
    val xShake = remember { Animatable(initialValue = 0.0F) }
    var passcodeRejectedDialogVisible by remember { mutableStateOf(false) }
    val biometricResult by promptManager.promptResults.collectAsState(
        initial = null
    )
    val enrollLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            println("Activity result: $it")
        }
    )

    LaunchedEffect(biometricResult) {
        if(biometricResult is BiometricPromptManager.BiometricResult.AuthenticationNotSet) {
            if(Build.VERSION.SDK_INT >= 30) {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                    )
                }
                enrollLauncher.launch(enrollIntent)
            }
        }
    }

    LaunchedEffect(true){
        if(preferenceManager.hasPasscode) {
            promptManager.showBiometricPrompt(
                title = context.getString(R.string.biometric_auth_title),
                description = context.getString(R.string.biometric_auth_description)
            )
        }
    }

    biometricResult?.let { result ->

        when(result) {
            is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                result.error
            }
            BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                Toast.makeText(context, R.string.authentication_failed, Toast.LENGTH_SHORT).show()
            }
            BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
                Toast.makeText(context, R.string.authentication_not_set, Toast.LENGTH_SHORT).show()
            }
            BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                onBiometricAuthenticated.invoke()
            }
            BiometricPromptManager.BiometricResult.FeatureUnavailable -> {
                Toast.makeText(context, R.string.authentication_not_available, Toast.LENGTH_SHORT).show()
            }
            BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
                Toast.makeText(context, R.string.authentication_not_available, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(key1 = viewModel.onPasscodeConfirmed) {
        viewModel.onPasscodeConfirmed.collect {
            onPasscodeConfirm(it)
        }
    }

    LaunchedEffect(key1 = viewModel.onPasscodeRejected) {
        viewModel.onPasscodeRejected.collect {
            passcodeRejectedDialogVisible = true
            vibrateFeedback(context)
            performShakeAnimation(xShake)
            onPasscodeRejected()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PasscodeToolbar(activeStep = activeStep, preferenceManager.hasPasscode)
        PasscodeSkipButton(
            onSkipButton = { onSkipButton.invoke() },
            hasPassCode = preferenceManager.hasPasscode
        )
        MifosIcon(modifier = Modifier.fillMaxWidth())
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PasscodeHeader(
                activeStep = activeStep,
                isPasscodeAlreadySet = preferenceManager.hasPasscode
            )
            PasscodeView(
                filledDots = filledDots,
                currentPasscode = currentPasscode,
                passcodeVisible = passcodeVisible,
                togglePasscodeVisibility = { viewModel.togglePasscodeVisibility() },
                restart = { viewModel.restart() },
                passcodeRejectedDialogVisible = passcodeRejectedDialogVisible,
                onDismissDialog = { passcodeRejectedDialogVisible = false },
                xShake = xShake
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        PasscodeKeys(
            enterKey = { viewModel.enterKey(it) },
            deleteKey = { viewModel.deleteKey() },
            deleteAllKeys = { viewModel.deleteAllKeys() },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        PasscodeForgotButton(
            onForgotButton = { onForgotButton.invoke() },
            hasPassCode = preferenceManager.hasPasscode
        )
        UseTouchIdButton(
            onClick = {
                promptManager.showBiometricPrompt(
                    title = context.getString(R.string.biometric_auth_title),
                    description = context.getString(R.string.biometric_auth_description)
                )
            },
            hasPassCode = preferenceManager.hasPasscode
        )

    }
}

@Composable
private fun PasscodeView(
    modifier: Modifier = Modifier,
    restart: () -> Unit,
    togglePasscodeVisibility: () -> Unit,
    filledDots: Int,
    passcodeVisible: Boolean,
    currentPasscode: String,
    passcodeRejectedDialogVisible: Boolean,
    onDismissDialog: () -> Unit,
    xShake: Animatable<Float, *>
) {
    PasscodeMismatchedDialog(
        visible = passcodeRejectedDialogVisible,
        onDismiss = {
            onDismissDialog.invoke()
            restart()
        }
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = modifier.offset(x = xShake.value.dp),
            horizontalArrangement = Arrangement.spacedBy(
                space = 26.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(PASSCODE_LENGTH) { dotIndex ->
                if (passcodeVisible && dotIndex < currentPasscode.length) {
                    Text(
                        text = currentPasscode[dotIndex].toString(),
                        color = blueTint
                    )
                } else {
                    val isFilledDot = dotIndex + 1 <= filledDots
                    val dotColor = animateColorAsState(
                        if (isFilledDot) blueTint else Color.Gray, label = ""
                    )

                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(
                                color = dotColor.value,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
        IconButton(
            onClick = { togglePasscodeVisibility.invoke() },
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Icon(
                imageVector = if (passcodeVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PasscodeScreenPreview() {
    PasscodeScreen(
        viewModel = PasscodeViewModel(object : PasscodeRepository {
            override fun getSavedPasscode(): String {
                return ""
            }

            override val hasPasscode: Boolean
                get() = false

            override fun savePasscode(passcode: String) {}

        }),
        {}, {}, {}, {}, {}, AppCompatActivity()
    )
}
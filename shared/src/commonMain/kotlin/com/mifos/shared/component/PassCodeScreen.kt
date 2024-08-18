package com.mifos.shared.component

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mifos.shared.utility.BioMetricUtil
import com.mifos.shared.utility.PreferenceManager
import com.mifos.shared.theme.blueTint
import com.mifos.shared.utility.Constants.PASSCODE_LENGTH
import com.mifos.shared.utility.ShakeAnimation.performShakeAnimation
import com.mifos.shared.utility.Step
import com.mifos.shared.viewmodels.BiometricAuthorizationViewModel
import com.mifos.shared.viewmodels.BiometricEffect
import com.mifos.shared.viewmodels.PasscodeViewModel
import com.mifos.shared.resources.Res
import com.mifos.shared.resources.biometric_registration_success
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * @author pratyush
 * @since 15/3/24
 */

@Composable
fun PasscodeScreen(
    viewModel: PasscodeViewModel = viewModel { PasscodeViewModel() },
    biometricAuthorizationViewModel: BiometricAuthorizationViewModel,
    bioMetricUtil: BioMetricUtil,
    onForgotButton: () -> Unit,
    onSkipButton: () -> Unit,
    onPasscodeConfirm: (String) -> Unit,
    onPasscodeRejected: () -> Unit,
    onBiometricAuthSuccess: () -> Unit
) {
    val preferenceManager = remember { PreferenceManager() }
    val activeStep by viewModel.activeStep.collectAsState()
    val filledDots by viewModel.filledDots.collectAsState()
    val passcodeVisible by viewModel.passcodeVisible.collectAsState()
    val currentPasscode by viewModel.currentPasscodeInput.collectAsState()
    val xShake = remember { Animatable(initialValue = 0.0F) }
    var passcodeRejectedDialogVisible by remember { mutableStateOf(false) }
    val biometricState by biometricAuthorizationViewModel.state.collectAsState()
    var biometricMessage by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    biometricState.error?.let {
        biometricMessage = it
    }

    LaunchedEffect(key1 = Unit) {
        biometricAuthorizationViewModel.effect.collectLatest {
            when (it) {
                BiometricEffect.BiometricAuthSuccess -> {
                    onBiometricAuthSuccess.invoke()
                }
                BiometricEffect.BiometricSetSuccess -> {
                    biometricMessage = getString( Res.string.biometric_registration_success )
                }
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
//              vibrateFeedback(context)
            performShakeAnimation(xShake)
            onPasscodeRejected()
        }
    }

    LaunchedEffect(activeStep) {
        if (activeStep == Step.Confirm) {
            biometricAuthorizationViewModel.setBiometricAuthorization(bioMetricUtil)
        }
    }

    LaunchedEffect(true){
        if(preferenceManager.hasPasscode)
        biometricAuthorizationViewModel.authorizeBiometric(bioMetricUtil)
    }

    val snackBarHostState = remember {
        SnackbarHostState()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
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
                    if(bioMetricUtil.isBiometricSet())
                        biometricAuthorizationViewModel.authorizeBiometric(bioMetricUtil)
                    else
                        biometricAuthorizationViewModel.setBiometricAuthorization(bioMetricUtil)
                },
                hasPassCode = preferenceManager.hasPasscode
            )

            LaunchedEffect ( biometricMessage ) {
                if(biometricMessage.isNotEmpty()) {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            message = biometricMessage,
                            duration = SnackbarDuration.Short,
                            withDismissAction = false,
                            actionLabel = "Ok"
                        )
                    }
                }
            }
        }
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

//@Preview(showBackground = true)
//@Composable
//fun PasscodeScreenPreview() {
//    PasscodeScreen(
//        viewModel = PasscodeViewModel(object : PasscodeRepository {
//            override fun getSavedPasscode(): String {
//                return ""
//            }
//
//            override val hasPasscode: Boolean
//                get() = false
//
//            override fun savePasscode(passcode: String) {}
//
//        }),
//        {}, {}, {}, {}
//    )
//}
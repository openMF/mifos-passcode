package com.mifos.compose.component

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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.compose.R
import com.mifos.compose.theme.blueTint
import com.mifos.compose.theme.forgotButtonStyle
import com.mifos.compose.theme.skipButtonStyle
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
    preferenceManager: PreferenceManager,
    onForgotButton: () -> Unit,
    onSkipButton: () -> Unit,
    onPasscodeConfirm: (String) -> Unit,
    onPasscodeRejected: () -> Unit,
) {
    val context = LocalContext.current
    val activeStep by viewModel.activeStep.collectAsStateWithLifecycle()
    val filledDots by viewModel.filledDots.collectAsStateWithLifecycle()
    val passcodeVisible by viewModel.passcodeVisible.collectAsStateWithLifecycle()
    val currentPasscode by viewModel.currentPasscodeInput.collectAsStateWithLifecycle()
    val xShake = remember { Animatable(initialValue = 0.0F) }
    var passcodeRejectedDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.onPasscodeConfirmed.collect {
            onPasscodeConfirm(it)
        }
    }
    LaunchedEffect(key1 = true) {
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
        if (!preferenceManager.hasPasscode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { onSkipButton.invoke() }
                ) {
                    Text(text = stringResource(R.string.skip), style = skipButtonStyle)
                }
            }
        }
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
        if (preferenceManager.hasPasscode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = { onForgotButton.invoke() }
                ) {
                    Text(
                        text = stringResource(R.string.forgot_passcode_login_manually),
                        style = forgotButtonStyle
                    )
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

@Preview(showBackground = true)
@Composable
fun PasscodeScreenPreview() {
    PasscodeScreen(
        viewModel = PasscodeViewModel(PreferenceManager(LocalContext.current)),
        PreferenceManager(LocalContext.current),
        {}, {}, {}, {}
    )
}
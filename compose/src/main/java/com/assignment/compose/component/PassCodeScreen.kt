package com.assignment.compose.component

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.assignment.compose.PasscodeViewModel
import com.assignment.compose.theme.keyTint
import com.assignment.compose.utility.InterfacePreviewProvider
import com.assignment.compose.utility.PasscodeListener
import com.assignment.compose.utility.PreferenceManager

const val VIBRATE_FEEDBACK_DURATION = 300L

/**
 * @author pratyush
 * @since 15/3/24
 */

@Composable
fun PasscodeScreen(
    viewModel: PasscodeViewModel,
    passcodeListener: PasscodeListener,
    preferenceManager: PreferenceManager,
) {

    val activeStep by viewModel.activeStep.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    viewModel.isPasscodeAlreadySet = preferenceManager.hasPasscode

    LaunchedEffect(key1 = true) {
        viewModel.onPasscodeConfirmed.collect {
            passcodeListener.onPassCodeReceive(it)
            snackbarHostState.showSnackbar(
                message = "Passcode successfully created"
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PasscodeToolbar(activeStep = activeStep, preferenceManager.hasPasscode)
        Spacer(modifier = Modifier.height(6.dp))
        MifosIcon(modifier = Modifier.fillMaxWidth())
        PasscodeHeader(
            activeStep = activeStep,
            isPasscodeAlreadySet = preferenceManager.hasPasscode
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 24.dp), horizontalArrangement = Arrangement.Center
        ) {
            PasscodeView(viewModel = viewModel, passcodeListener = passcodeListener)
        }
        Spacer(modifier = Modifier.height(6.dp))
        PasscodeKeys(
            viewModel = viewModel,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PasscodeView(
    modifier: Modifier = Modifier,
    viewModel: PasscodeViewModel,
    passcodeListener: PasscodeListener
) {
    val context = LocalContext.current
    val filledDots by viewModel.filledDots.collectAsState()
    val xShake = remember { Animatable(initialValue = 0.0F) }
    val passcodeRejectedDialogVisible = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.onPasscodeRejected.collect {
            passcodeRejectedDialogVisible.value = true
            vibrateFeedback(context)

            xShake.animateTo(
                targetValue = 0.dp.value,
                animationSpec = keyframes {
                    0.0F at 0
                    20.0F at 80
                    -20.0F at 120
                    10.0F at 160
                    -10.0F at 200
                    5.0F at 240
                    0.0F at 280
                }
            )
            passcodeListener.onPasscodeReject()
        }
    }

    PasscodeMismatchedDialog(
        visible = passcodeRejectedDialogVisible.value,
        onDismiss = {
            passcodeRejectedDialogVisible.value = false
            viewModel.restart()
        }
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.offset(x = xShake.value.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = 26.dp,
                alignment = Alignment.CenterHorizontally
            )
        ) {
            repeat(PasscodeViewModel.PASSCODE_LENGTH) { dot ->
                val isFilledDot = dot + 1 <= filledDots
                val dotColor = animateColorAsState(
                    if (isFilledDot) {
                        keyTint
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }, label = ""
                )

                Row(
                    modifier = modifier
                        .size(size = 14.dp)
                        .background(
                            color = dotColor.value,
                            shape = CircleShape
                        )
                ) {}
            }
        }
    }
}

@Suppress("DEPRECATION")
fun vibrateFeedback(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).vibrate(
            CombinedVibration.createParallel(
                VibrationEffect.createOneShot(
                    VIBRATE_FEEDBACK_DURATION,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        )
    } else {
        (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(
                    VibrationEffect.createOneShot(
                        VIBRATE_FEEDBACK_DURATION,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                it.vibrate(VIBRATE_FEEDBACK_DURATION)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PassCode(@PreviewParameter(InterfacePreviewProvider::class) passcodeListener: PasscodeListener) {
    PasscodeScreen(
        viewModel = PasscodeViewModel(PreferenceManager(LocalContext.current)),
        passcodeListener,
        PreferenceManager(LocalContext.current)
    )
}
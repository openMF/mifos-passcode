package com.assignment.compose.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.assignment.compose.viewmodels.PasscodeViewModel
import com.assignment.compose.R

@Composable
fun PasscodeToolbar(activeStep: PasscodeViewModel.Step, hasPasscode: Boolean) {
    val exitWarningDialogVisible = remember { mutableStateOf(false) }
    ExitWarningDialog(
        visible = exitWarningDialogVisible.value,
        onConfirm = {},
        onDismiss = {
            exitWarningDialogVisible.value = false
        }
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        if(!hasPasscode) {
            PasscodeStepIndicator(
                activeStep = activeStep
            )
        }
    }
}

@Composable
fun ExitWarningDialog(
    visible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (visible) {
        AlertDialog(
            shape = MaterialTheme.shapes.large,
            title = {
                Text(text = "Are you sure you want to exit?")
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(text = stringResource(R.string.exit))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            onDismissRequest = onDismiss
        )
    }
}
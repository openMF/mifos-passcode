package com.mifos.compose.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mifos.compose.R

@Composable
fun PasscodeMismatchedDialog(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    if (visible) {
        AlertDialog(
            shape = MaterialTheme.shapes.large,
            title = { Text(text = "Passcodes do not match!") },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.try_again))
                }
            },
            onDismissRequest = onDismiss
        )
    }
}
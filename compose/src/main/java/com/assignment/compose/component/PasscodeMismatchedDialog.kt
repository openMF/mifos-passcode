package com.assignment.compose.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

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
                    Text(text = "Try again")
                }
            },
            onDismissRequest = onDismiss
        )
    }
}
package com.mifos.mobile.passcode

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton

class FpAuthDialog(private val context: Context) {
    private var dialogTitle = context.getString(R.string.fp_dialog_title)
    private var dialogMessage = context.getString(R.string.fp_dialog_message)
    private var dialogCancelText = context.getString(R.string.fp_dialog_cancel)

    private var layoutInflater = LayoutInflater.from(context)
    private var dialogView = layoutInflater.inflate(R.layout.dialog_fingerprint_auth, null)
    private var tvDialogTitle = dialogView.findViewById<TextView>(R.id.fingerprint_dialog_title)
    private var tvDialogMessage = dialogView.findViewById<TextView>(R.id.fingerprint_dialog_message)
    private var tvDialogStatus = dialogView.findViewById<TextView>(R.id.fingerprint_dialog_status)
    private var btnCancel = dialogView.findViewById<AppCompatButton>(R.id.fingerprint_dialog_cancel)

    private var dialogBuilder = AlertDialog.Builder(context)
    private var dialog: AlertDialog? = null

    private var fpAuthCallback: FpAuthCallback = object : FpAuthCallback {
        override fun onFpAuthSuccess() {
            Toast.makeText(context, context.getString(R.string.authentication_successful), Toast.LENGTH_SHORT).show()
        }

        override fun onCancel() {
            Toast.makeText(context, context.getString(R.string.authentication_cancelled), Toast.LENGTH_SHORT).show()
        }

    }
    private var fpAuthHelper = FpAuthHelper(context, fpAuthCallback, this)

    fun setTitle(title: String): FpAuthDialog {
        dialogTitle = title
        return this
    }

    fun setTitle(resTitle: Int): FpAuthDialog {
        dialogTitle = context.resources.getString(resTitle)
        return this
    }

    fun setMessage(message: String): FpAuthDialog {
        dialogMessage = message
        return this
    }

    fun setMessage(resMessage: Int): FpAuthDialog {
        dialogMessage = context.resources.getString(resMessage)
        return this
    }

    fun setCancelText(cancelText: String): FpAuthDialog {
        dialogCancelText = cancelText
        return this
    }

    fun setCancelText(resCancelText: Int): FpAuthDialog {
        dialogCancelText = context.resources.getString(resCancelText)
        return this
    }

    fun setCallback(fpAuthCallback: FpAuthCallback): FpAuthDialog {
        this.fpAuthCallback = fpAuthCallback
        fpAuthHelper = FpAuthHelper(context, fpAuthCallback, this)
        return this
    }

    internal fun setStatusText(statusText: String) {
        tvDialogStatus.text = statusText
    }

    internal fun setStatusIcon(resIcon: Int) {
        tvDialogStatus.setCompoundDrawablesWithIntrinsicBounds(resIcon, 0, 0, 0)
    }

    fun dismiss() {
        fpAuthHelper.stopFpAuth()
        dialog!!.dismiss()
    }

    fun show() {
        if (!FpAuthSupport.checkAvailabiltyAndIfFingerprintRegistered(context)) {
            Log.e("FingerprintAuth", "Device not Suitable for Fingerprint Authentication")
            return
        }
        tvDialogTitle.text = dialogTitle
        tvDialogMessage.text = dialogMessage
        btnCancel.text = dialogCancelText

        dialog = dialogBuilder.setView(dialogView).create()
        dialog!!.setCancelable(false)
        dialog!!.show()

        fpAuthHelper.startFpAuth()

        btnCancel.setOnClickListener {
            fpAuthHelper.stopFpAuth()
            dialog!!.dismiss()
            fpAuthCallback.onCancel()
        }
    }
}
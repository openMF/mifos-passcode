package com.mifos.passcode

import android.view.View
import android.widget.Toast
import com.mifos.mobile.passcode.MifosPassCodeActivity
import com.mifos.mobile.passcode.utils.EncryptionUtil

/**
 * Created by dilpreet on 19/01/18.
 */
class PassCodeActivity : MifosPassCodeActivity() {

    override val logo: Int
        get() =//logo to be shown on the top
            R.drawable.mifos_logo

    override fun startNextActivity() {
        //start intent for the next activity
    }

    override fun startLoginActivity() {
        //start intent for the login activity
    }

    override fun showToaster(view: View?, msg: Int) {
        //show prompts in toast or using snackbar
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override val encryptionType: Int = EncryptionUtil.DEFAULT

    override val fpDialogTitle: String
        get() =//Title to be shown for Fingerprint Dialog
            getString(R.string.fingerprint_dialog_title)
}
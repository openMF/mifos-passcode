package com.mifos.mobile.passcode

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.mifos.mobile.passcode.FpAuthSupport.checkAvailabiltyAndIfFingerprintRegistered
import com.mifos.mobile.passcode.MifosPassCodeView.PassCodeListener
import com.mifos.mobile.passcode.utils.EncryptionUtil
import com.mifos.mobile.passcode.utils.EncryptionUtil.TYPE
import com.mifos.mobile.passcode.utils.EncryptionUtil.getAndroidClientHash
import com.mifos.mobile.passcode.utils.EncryptionUtil.getDefaultHash
import com.mifos.mobile.passcode.utils.EncryptionUtil.getFineractCNHash
import com.mifos.mobile.passcode.utils.EncryptionUtil.getMobileBankingHash
import com.mifos.mobile.passcode.utils.PassCodeConstants
import com.mifos.mobile.passcode.utils.PassCodeNetworkChecker.isConnected
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper

abstract class MifosPassCodeActivity : AppCompatActivity(), PassCodeListener {
    private var clRootview: NestedScrollView? = null
    private var btnForgotPasscode: AppCompatButton? = null
    private var mifosPassCodeView: MifosPassCodeView? = null
    private var btnSkip: AppCompatButton? = null
    private var btnSave: AppCompatButton? = null
    private var tvPasscodeIntro: TextView? = null
    private var ivVisibility: ImageView? = null
    private var ivLogo: ImageView? = null
    private var shakeAnimation: Animation? = null
    private var counter = 0
    private var isInitialScreen = false
    private var isPassCodeVerified = false
    private var strPassCodeEntered: String? = null
    private var passcodePreferencesHelper: PasscodePreferencesHelper? = null
    private var resetPasscode = false
    abstract val logo: Int
    abstract val fpDialogTitle: String?
    abstract fun startNextActivity()
    abstract fun startLoginActivity()
    abstract fun showToaster(view: View?, msg: Int)

    @get:TYPE
    abstract val encryptionType: Int
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_code)
        clRootview = findViewById(R.id.cl_rootview)
        btnForgotPasscode = findViewById(R.id.btn_forgot_passcode)
        mifosPassCodeView = findViewById(R.id.pv_passcode)
        btnSkip = findViewById(R.id.btn_skip)
        btnSave = findViewById(R.id.btn_save)
        tvPasscodeIntro = findViewById(R.id.tv_passcode)
        ivVisibility = findViewById(R.id.iv_visibility)
        ivLogo = findViewById(R.id.iv_logo)
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake)
        ivLogo?.setImageResource(logo)
        passcodePreferencesHelper = PasscodePreferencesHelper(this)
        isInitialScreen = intent.getBooleanExtra(
            PassCodeConstants.PASSCODE_INITIAL_LOGIN,
            false
        )

        //Show Prompt Dialog if device Support Fingerprint Authentication and has fingerprint
        // registered
        if (checkAvailabiltyAndIfFingerprintRegistered(this)
            && passcodePreferencesHelper!!.fingerprintEnableDialogState
        ) {
            val builder = AlertDialog.Builder(
                this,
                R.style.MaterialAlertDialogStyle
            )
            builder.setTitle(R.string.fingerprint)
            builder.setMessage(R.string.FingerprintEnableMessage)
            builder.setPositiveButton("Yes") { dialogInterface, i ->
                passcodePreferencesHelper!!.fingerprintEnableDialogState = false
                passcodePreferencesHelper!!.authType = "fpauth"
                FpAuthDialog(this@MifosPassCodeActivity)
                    .setTitle(fpDialogTitle!!)
                    .setCallback(object : FpAuthCallback {
                        override fun onFpAuthSuccess() {
                            startHomeActivity()
                        }

                        override fun onCancel() {
                            cancelFingerprintAuth()
                        }
                    }).show()
            }
            builder.setNegativeButton("No") { dialogInterface, i ->
                passcodePreferencesHelper!!.fingerprintEnableDialogState = false
                passcodePreferencesHelper!!.authType = "passcode"
            }
            val alertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
        if (passcodePreferencesHelper!!.authType.equals("passcode", ignoreCase = true)) {
            resetPasscode = intent.getBooleanExtra(PassCodeConstants.RESET_PASSCODE, false)
            isPassCodeVerified = false
            strPassCodeEntered = ""
            if (!passcodePreferencesHelper!!.passCode!!.isEmpty()) {
                btnSkip?.visibility = View.GONE
                btnSave?.visibility = View.GONE
                tvPasscodeIntro?.visibility = View.GONE
                btnForgotPasscode?.visibility = View.VISIBLE
                //enabling passCodeListener only when user has already setup PassCode
                mifosPassCodeView?.setPassCodeListener(this)
            }
            if (resetPasscode) {
                btnSkip?.visibility = View.GONE
                btnSave?.visibility = View.GONE
                tvPasscodeIntro?.visibility = View.VISIBLE
                tvPasscodeIntro?.setText(R.string.confirm_passcode)
            }
        }
    }

    private fun encryptPassCode(passCode: String): String? {
        @TYPE val type = encryptionType
        var encryptedPassCode: String? = null
        when (type) {
            EncryptionUtil.MOBILE_BANKING -> encryptedPassCode = getMobileBankingHash(passCode)
            EncryptionUtil.ANDROID_CLIENT -> encryptedPassCode = getAndroidClientHash(passCode)
            EncryptionUtil.FINERACT_CN -> encryptedPassCode = getFineractCNHash(passCode)
            EncryptionUtil.DEFAULT -> encryptedPassCode = getDefaultHash(passCode)
        }
        return encryptedPassCode
    }

    fun clearTokenPreferences() {
        passcodePreferencesHelper!!.clear()
    }

    fun skip(v: View?) {
        startHomeActivity()
    }

    /**
     * Saves the passcode by encrypting it which we got from [MifosPassCodeView]
     *
     * @param view Passcode View
     */
    fun savePassCode(view: View?) {
        if (isPassCodeLengthCorrect) {
            if (isPassCodeVerified) {
                if (strPassCodeEntered!!.compareTo(mifosPassCodeView!!.passcode) == 0) {
                    passcodePreferencesHelper!!.savePassCode(encryptPassCode(mifosPassCodeView!!.passcode))
                    startHomeActivity()
                } else {
                    showToaster(clRootview, R.string.passcode_does_not_match)
                    mifosPassCodeView!!.clearPasscodeField()
                }
            } else {
                btnSkip!!.visibility = View.INVISIBLE
                btnSave!!.text = getString(R.string.save)
                tvPasscodeIntro!!.text = getString(R.string.reenter_passcode)
                strPassCodeEntered = mifosPassCodeView!!.passcode
                mifosPassCodeView!!.clearPasscodeField()
                isPassCodeVerified = true
            }
        }
    }

    /**
     * It is a callback for [MifosPassCodeView], provides with the passcode entered by user
     *
     * @param passcode Passcode that is entered by user.
     */
    override fun passCodeEntered(passcode: String?) {
        if (!isInternetAvailable) {
            mifosPassCodeView!!.clearPasscodeField()
            return
        }
        if (counter == 3) {
            Toast.makeText(
                applicationContext, R.string.incorrect_passcode_more_than_three,
                Toast.LENGTH_SHORT
            ).show()
            clearTokenPreferences()
            startLoginActivity()
            return
        }
        if (isPassCodeLengthCorrect) {
            val passwordEntered = encryptPassCode(mifosPassCodeView!!.passcode)
            if (passcodePreferencesHelper!!.passCode == passwordEntered) {
                if (resetPasscode) {
                    resetPasscode()
                    return
                }
                startHomeActivity()
            } else {
                mifosPassCodeView!!.startAnimation(shakeAnimation)
                counter++
                mifosPassCodeView!!.clearPasscodeField()
                showToaster(clRootview, R.string.incorrect_passcode)
            }
        }
    }

    fun forgotPassCode(v: View?) {
        clearTokenPreferences()
        startLoginActivity()
    }

    fun cancelFingerprintAuth() {
        clearTokenPreferences()
        startLoginActivity()
        finish()
    }

    /**
     * Checks for internet availability
     *
     * @return Returns true if connected else returns false
     */
    private val isInternetAvailable: Boolean
        get() = if (isConnected(this)) {
            true
        } else {
            showToaster(clRootview, R.string.no_internet_connection)
            false
        }

    fun clickedOne(v: View?) {
        mifosPassCodeView!!.enterCode(getString(R.string.one))
    }

    fun clickedTwo(v: View?) {
        mifosPassCodeView!!.enterCode(getString(R.string.two))
    }

    fun clickedThree(v: View?) {
        mifosPassCodeView!!.enterCode(getString(R.string.three))
    }

    fun clickedFour(v: View?) {
        mifosPassCodeView!!.enterCode(getString(R.string.four))
    }

    fun clickedFive(v: View?) {
        mifosPassCodeView!!.enterCode(getString(R.string.five))
    }

    fun clickedSix(v: View?) {
        mifosPassCodeView!!.enterCode(getString(R.string.six))
    }

    fun clickedSeven(v: View?) {
        mifosPassCodeView!!.enterCode(getString(R.string.seven))
    }

    fun clickedEight(v: View?) {
        mifosPassCodeView!!.enterCode(getString(R.string.eight))
    }

    fun clickedNine(v: View?) {
        mifosPassCodeView!!.enterCode(getString(R.string.nine))
    }

    fun clickedZero(v: View?) {
        mifosPassCodeView!!.enterCode(getString(R.string.zero))
    }

    fun clickedBackSpace(v: View?) {
        mifosPassCodeView!!.backSpace()
    }

    /**
     * @param view PasscodeView that changes to text if it was hidden and vice a versa
     */
    fun visibilityChange(view: View?) {
        mifosPassCodeView!!.revertPassCodeVisibility()
        if (!mifosPassCodeView!!.passcodeVisible()) {
            ivVisibility!!.setColorFilter(
                ContextCompat.getColor(
                    this@MifosPassCodeActivity,
                    R.color.light_grey
                )
            )
        } else {
            ivVisibility!!.setColorFilter(
                ContextCompat.getColor(
                    this@MifosPassCodeActivity,
                    R.color.gray_dark
                )
            )
        }
    }

    /**
     * Checks whether passcode entered is of correct length
     *
     * @return Returns true if passcode lenght is 4 else shows message
     */
    private val isPassCodeLengthCorrect: Boolean
        get() {
            if (mifosPassCodeView!!.passcode.length == 4) {
                return true
            }
            showToaster(clRootview, R.string.error_passcode)
            return false
        }

    private fun startHomeActivity() {
        if (isInitialScreen) {
            startNextActivity()
        }
        finish()
    }

    override fun onBackPressed() {
        if (isInitialScreen) {
            super.onBackPressed()
        }
    }

    private fun resetPasscode() {
        resetPasscode = false
        btnSkip!!.visibility = View.VISIBLE
        btnSave!!.visibility = View.VISIBLE
        tvPasscodeIntro!!.setText(R.string.passcode_setup)
        counter = 0
        mifosPassCodeView!!.clearPasscodeField()
        mifosPassCodeView!!.setPassCodeListener(null)
        passcodePreferencesHelper!!.clear()
    }

    override fun onResume() {
        super.onResume()
        if (passcodePreferencesHelper!!.authType.equals("fpauth", ignoreCase = true)) {
            FpAuthDialog(this@MifosPassCodeActivity)
                .setTitle(fpDialogTitle!!)
                .setCallback(object : FpAuthCallback {
                    override fun onFpAuthSuccess() {
                        startHomeActivity()
                    }

                    override fun onCancel() {
                        cancelFingerprintAuth()
                    }
                }).show()
        }
    }

    companion object {
        @JvmOverloads
        fun startMifosPassCodeActivity(
            context: Context, clazz: Class<*>?,
            isInitialLogin: Boolean = true
        ) {
            val intent = Intent(context, clazz)
            intent.putExtra(PassCodeConstants.PASSCODE_INITIAL_LOGIN, isInitialLogin)
            context.startActivity(intent)
        }
    }
}
package com.mifos.mobile.passcode

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mifos.mobile.passcode.FpAuthSupport.checkAvailabiltyAndIfFingerprintRegistered
import com.mifos.mobile.passcode.databinding.ActivityPassCodeBinding
import com.mifos.mobile.passcode.utils.EncryptionUtil
import com.mifos.mobile.passcode.utils.EncryptionUtil.TYPE
import com.mifos.mobile.passcode.utils.PassCodeConstants
import com.mifos.mobile.passcode.utils.PassCodeNetworkChecker.isConnected
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper

abstract class MifosPassCodeActivity : AppCompatActivity(), PassCodeListener {

    var shakeAnimation: Animation? = null
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

    private lateinit var binding: ActivityPassCodeBinding

    @get:TYPE
    abstract val encryptionType: Int
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPassCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake)
        binding.ivLogo.setImageResource(logo)
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
            builder.setIcon(R.drawable.ic_fingerprint_blue_48dp)
            builder.setMessage(R.string.FingerprintEnableMessage)
            builder.setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface, i: Int) {
                    passcodePreferencesHelper!!.fingerprintEnableDialogState = false
                    passcodePreferencesHelper!!.authType = "fpauth"
                    fpDialogTitle?.let {
                        FpAuthDialog(this@MifosPassCodeActivity)
                            .setTitle(it)
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
            })
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
                binding.btnSkip.visibility = View.GONE
                binding.btnSave.visibility = View.GONE
                binding.tvPasscode.visibility = View.GONE
                binding.btnForgotPasscode.visibility = View.VISIBLE
                //enabling passCodeListener only when user has already setup PassCode
                binding.pvPasscode.setPassCodeListener(this)
            }
            if (resetPasscode) {
                binding.btnSkip.visibility = View.GONE
                binding.btnSave.visibility = View.GONE
                binding.tvPasscode.visibility = View.VISIBLE
                binding.tvPasscode.text = getString(R.string.confirm_passcode)
            }
        }
    }

    private fun encryptPassCode(passCode: String): String? {
        @TYPE val type = encryptionType
        var encryptedPassCode: String? = null
        when (type) {
            EncryptionUtil.MOBILE_BANKING -> encryptedPassCode =
                EncryptionUtil.getMobileBankingHash(passCode)

            EncryptionUtil.ANDROID_CLIENT -> encryptedPassCode =
                EncryptionUtil.getAndroidClientHash(passCode)

            EncryptionUtil.FINERACT_CN -> encryptedPassCode =
                EncryptionUtil.getFineractCNHash(passCode)

            EncryptionUtil.DEFAULT -> encryptedPassCode = EncryptionUtil.getDefaultHash(passCode)
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
                if (strPassCodeEntered!!.compareTo(binding.pvPasscode.passcode) == 0) {
                    passcodePreferencesHelper!!.savePassCode(encryptPassCode(binding.pvPasscode.passcode))
                    startHomeActivity()
                } else {
                    showToaster(binding.clRootview, R.string.passcode_does_not_match)
                    binding.pvPasscode.clearPasscodeField()
                }
            } else {
                binding.btnSkip.visibility = View.INVISIBLE
                binding.btnSave.text = getString(R.string.save)
                binding.tvPasscode.text = getString(R.string.reenter_passcode)
                strPassCodeEntered = binding.pvPasscode.passcode
                binding.pvPasscode.clearPasscodeField()
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
            binding.pvPasscode.clearPasscodeField()
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
            val passwordEntered = encryptPassCode(binding.pvPasscode.passcode)
            if (passcodePreferencesHelper!!.passCode == passwordEntered) {
                if (resetPasscode) {
                    resetPasscode()
                    return
                }
                startHomeActivity()
            } else {
                binding.pvPasscode.startAnimation(shakeAnimation)
                counter++
                binding.pvPasscode.clearPasscodeField()
                showToaster(binding.clRootview, R.string.incorrect_passcode)
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

    private val isInternetAvailable: Boolean
        /**
         * Checks for internet availability
         *
         * @return Returns true if connected else returns false
         */
        get() = if (isConnected(this)) {
            true
        } else {
            showToaster(binding.clRootview, R.string.no_internet_connection)
            false
        }

    fun clickedOne(v: View?) {
        binding.pvPasscode.enterCode(getString(R.string.one))
    }

    fun clickedTwo(v: View?) {
        binding.pvPasscode.enterCode(getString(R.string.two))
    }

    fun clickedThree(v: View?) {
        binding.pvPasscode.enterCode(getString(R.string.three))
    }

    fun clickedFour(v: View?) {
        binding.pvPasscode.enterCode(getString(R.string.four))
    }

    fun clickedFive(v: View?) {
        binding.pvPasscode.enterCode(getString(R.string.five))
    }

    fun clickedSix(v: View?) {
        binding.pvPasscode.enterCode(getString(R.string.six))
    }

    fun clickedSeven(v: View?) {
        binding.pvPasscode.enterCode(getString(R.string.seven))
    }

    fun clickedEight(v: View?) {
        binding.pvPasscode.enterCode(getString(R.string.eight))
    }

    fun clickedNine(v: View?) {
        binding.pvPasscode.enterCode(getString(R.string.nine))
    }

    fun clickedZero(v: View?) {
        binding.pvPasscode.enterCode(getString(R.string.zero))
    }

    fun clickedBackSpace(v: View?) {
        binding.pvPasscode.backSpace()
    }

    /**
     * @param view PasscodeView that changes to text if it was hidden and vice a versa
     */
    fun visibilityChange(view: View?) {
        binding.pvPasscode.revertPassCodeVisibility()
        if (!binding.pvPasscode.passcodeVisible()) {
            binding.ivVisibility.setColorFilter(
                ContextCompat.getColor(
                    this@MifosPassCodeActivity,
                    R.color.light_grey
                )
            )
        } else {
            binding.ivVisibility.setColorFilter(
                ContextCompat.getColor(
                    this@MifosPassCodeActivity,
                    R.color.gray_dark
                )
            )
        }
    }

    private val isPassCodeLengthCorrect: Boolean
        /**
         * Checks whether passcode entered is of correct length
         *
         * @return Returns true if passcode lenght is 4 else shows message
         */
        private get() {
            if (binding.pvPasscode.passcode.length == 4) {
                return true
            }
            showToaster(binding.clRootview, R.string.error_passcode)
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
        binding.btnSkip.visibility = View.VISIBLE
        binding.btnSave.visibility = View.VISIBLE
        binding.tvPasscode.setText(R.string.passcode_setup)
        counter = 0
        binding.pvPasscode.clearPasscodeField()
        binding.pvPasscode.setPassCodeListener(null)
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
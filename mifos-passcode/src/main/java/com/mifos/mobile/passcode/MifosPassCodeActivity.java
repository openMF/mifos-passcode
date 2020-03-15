package com.mifos.mobile.passcode;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mobile.passcode.utils.EncryptionUtil;
import com.mifos.mobile.passcode.utils.PassCodeConstants;
import com.mifos.mobile.passcode.utils.PassCodeNetworkChecker;
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.NestedScrollView;

public abstract class MifosPassCodeActivity extends AppCompatActivity implements MifosPassCodeView.
        PassCodeListener {

    NestedScrollView clRootview;
    AppCompatButton btnForgotPasscode;
    MifosPassCodeView mifosPassCodeView;
    AppCompatButton btnSkip;
    AppCompatButton btnSave;
    TextView tvPasscodeIntro;
    ImageView ivVisibility;
    ImageView ivLogo;
    Animation shakeAnimation;
    private int counter = 0;
    private boolean isInitialScreen;
    private boolean isPassCodeVerified;
    private String strPassCodeEntered;
    private PasscodePreferencesHelper passcodePreferencesHelper;
    private boolean resetPasscode;

    public abstract int getLogo();

    public abstract String getFpDialogTitle();

    public abstract void startNextActivity();

    public abstract void startLoginActivity();

    public abstract void showToaster(View view, int msg);

    @EncryptionUtil.TYPE
    public abstract int getEncryptionType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_code);

        clRootview = findViewById(R.id.cl_rootview);
        btnForgotPasscode = findViewById(R.id.btn_forgot_passcode);
        mifosPassCodeView = findViewById(R.id.pv_passcode);
        btnSkip = findViewById(R.id.btn_skip);
        btnSave = findViewById(R.id.btn_save);
        tvPasscodeIntro = findViewById(R.id.tv_passcode);
        ivVisibility = findViewById(R.id.iv_visibility);
        ivLogo = findViewById(R.id.iv_logo);
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);

        ivLogo.setImageResource(getLogo());
        passcodePreferencesHelper = new PasscodePreferencesHelper(this);
        isInitialScreen = getIntent().getBooleanExtra(PassCodeConstants.PASSCODE_INITIAL_LOGIN,
                false);

        //Show Prompt Dialog if device Support Fingerprint Authentication and has fingerprint
        // registered
        if (FpAuthSupport.checkAvailabiltyAndIfFingerprintRegistered(this)
                && passcodePreferencesHelper.getFingerprintEnableDialogState()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this,
                    R.style.MaterialAlertDialogStyle);
            builder.setTitle(R.string.fingerprint);
            builder.setMessage(R.string.FingerprintEnableMessage);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    passcodePreferencesHelper.setFingerprintEnableDialogState(false);
                    passcodePreferencesHelper.setAuthType("fpauth");
                    new FpAuthDialog(MifosPassCodeActivity.this)
                            .setTitle(getFpDialogTitle())
                            .setCallback(new FpAuthCallback() {
                                @Override
                                public void onFpAuthSuccess() {
                                    startHomeActivity();
                                }

                                @Override
                                public void onCancel() {
                                    cancelFingerprintAuth();
                                }
                            }).show();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    passcodePreferencesHelper.setFingerprintEnableDialogState(false);
                    passcodePreferencesHelper.setAuthType("passcode");
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }

        if (passcodePreferencesHelper.getAuthType().equalsIgnoreCase("passcode")) {
            resetPasscode = getIntent().getBooleanExtra(PassCodeConstants.RESET_PASSCODE, false);
            isPassCodeVerified = false;
            strPassCodeEntered = "";

            if (!passcodePreferencesHelper.getPassCode().isEmpty()) {
                btnSkip.setVisibility(View.GONE);
                btnSave.setVisibility(View.GONE);
                tvPasscodeIntro.setVisibility(View.GONE);
                btnForgotPasscode.setVisibility(View.VISIBLE);
                //enabling passCodeListener only when user has already setup PassCode
                mifosPassCodeView.setPassCodeListener(this);
            }

            if (resetPasscode) {
                btnSkip.setVisibility(View.GONE);
                btnSave.setVisibility(View.GONE);
                tvPasscodeIntro.setVisibility(View.VISIBLE);
                tvPasscodeIntro.setText(R.string.confirm_passcode);
            }
        }
    }

    private String encryptPassCode(String passCode) {
        @EncryptionUtil.TYPE int type = getEncryptionType();
        String encryptedPassCode = null;
        switch (type) {
            case EncryptionUtil.MOBILE_BANKING:
                encryptedPassCode = EncryptionUtil.getMobileBankingHash(passCode);
                break;
            case EncryptionUtil.ANDROID_CLIENT:
                encryptedPassCode = EncryptionUtil.getAndroidClientHash(passCode);
                break;
            case EncryptionUtil.FINERACT_CN:
                encryptedPassCode = EncryptionUtil.getFineractCNHash(passCode);
                break;
            case EncryptionUtil.DEFAULT:
                encryptedPassCode = EncryptionUtil.getDefaultHash(passCode);
                break;
        }

        return encryptedPassCode;
    }

    public void clearTokenPreferences() {
        passcodePreferencesHelper.clear();
    }

    public void skip(View v) {
        startHomeActivity();
    }

    /**
     * Saves the passcode by encrypting it which we got from {@link MifosPassCodeView}
     *
     * @param view Passcode View
     */
    public void savePassCode(View view) {
        if (isPassCodeLengthCorrect()) {
            if (isPassCodeVerified) {
                if (strPassCodeEntered.compareTo(mifosPassCodeView.getPasscode()) == 0) {
                    passcodePreferencesHelper.savePassCode(encryptPassCode(mifosPassCodeView.
                            getPasscode()));
                    startHomeActivity();
                } else {
                    showToaster(clRootview, R.string.passcode_does_not_match);
                    mifosPassCodeView.clearPasscodeField();
                }
            } else {
                btnSkip.setVisibility(View.INVISIBLE);
                btnSave.setText(getString(R.string.save));
                tvPasscodeIntro.setText(getString(R.string.reenter_passcode));
                strPassCodeEntered = mifosPassCodeView.getPasscode();
                mifosPassCodeView.clearPasscodeField();
                isPassCodeVerified = true;
            }
        }
    }


    /**
     * It is a callback for {@link MifosPassCodeView}, provides with the passcode entered by user
     *
     * @param passcode Passcode that is entered by user.
     */
    @Override
    public void passCodeEntered(String passcode) {

        if (!isInternetAvailable()) {
            mifosPassCodeView.clearPasscodeField();
            return;
        }

        if (counter == 3) {
            Toast.makeText(getApplicationContext(), R.string.incorrect_passcode_more_than_three,
                    Toast.LENGTH_SHORT).show();
            clearTokenPreferences();
            startLoginActivity();
            return;
        }

        if (isPassCodeLengthCorrect()) {
            String passwordEntered = encryptPassCode(mifosPassCodeView.getPasscode());
            if (passcodePreferencesHelper.getPassCode().equals(passwordEntered)) {
                if (resetPasscode) {
                    resetPasscode();
                    return;
                }
                startHomeActivity();
            } else {
                mifosPassCodeView.startAnimation(shakeAnimation);
                counter++;
                mifosPassCodeView.clearPasscodeField();
                showToaster(clRootview, R.string.incorrect_passcode);
            }
        }
    }


    public void forgotPassCode(View v) {
        clearTokenPreferences();
        startLoginActivity();
    }

    public void cancelFingerprintAuth() {
        clearTokenPreferences();
        startLoginActivity();
        finish();
    }

    /**
     * Checks for internet availability
     *
     * @return Returns true if connected else returns false
     */
    private boolean isInternetAvailable() {
        if (PassCodeNetworkChecker.isConnected(this)) {
            return true;
        } else {
            showToaster(clRootview, R.string.no_internet_connection);
            return false;
        }
    }

    public void clickedOne(View v) {
        mifosPassCodeView.enterCode(getString(R.string.one));
    }

    public void clickedTwo(View v) {
        mifosPassCodeView.enterCode(getString(R.string.two));
    }

    public void clickedThree(View v) {
        mifosPassCodeView.enterCode(getString(R.string.three));
    }

    public void clickedFour(View v) {
        mifosPassCodeView.enterCode(getString(R.string.four));
    }

    public void clickedFive(View v) {
        mifosPassCodeView.enterCode(getString(R.string.five));
    }

    public void clickedSix(View v) {
        mifosPassCodeView.enterCode(getString(R.string.six));
    }

    public void clickedSeven(View v) {
        mifosPassCodeView.enterCode(getString(R.string.seven));
    }

    public void clickedEight(View v) {
        mifosPassCodeView.enterCode(getString(R.string.eight));
    }

    public void clickedNine(View v) {
        mifosPassCodeView.enterCode(getString(R.string.nine));
    }

    public void clickedZero(View v) {
        mifosPassCodeView.enterCode(getString(R.string.zero));
    }

    public void clickedBackSpace(View v) {
        mifosPassCodeView.backSpace();
    }

    /**
     * @param view PasscodeView that changes to text if it was hidden and vice a versa
     */
    public void visibilityChange(View view) {
        mifosPassCodeView.revertPassCodeVisibility();
        if (!mifosPassCodeView.passcodeVisible()) {
            ivVisibility.setImageResource(R.drawable.ic_visibility_on_48px);
        } else {
            ivVisibility.setImageResource(R.drawable.ic_visibility_off_24dp);
        }
    }

    /**
     * Checks whether passcode entered is of correct length
     *
     * @return Returns true if passcode lenght is 4 else shows message
     */
    private boolean isPassCodeLengthCorrect() {
        if (mifosPassCodeView.getPasscode().length() == 4) {
            return true;
        }
        showToaster(clRootview, R.string.error_passcode);
        return false;
    }


    private void startHomeActivity() {
        if (isInitialScreen) {
            startNextActivity();
        }
        finish();
    }

    public static void startMifosPassCodeActivity(Context context, Class clazz) {
        startMifosPassCodeActivity(context, clazz, true);
    }

    public static void startMifosPassCodeActivity(Context context, Class clazz,
                                                  boolean isInitialLogin) {
        Intent intent = new Intent(context, clazz);
        intent.putExtra(PassCodeConstants.PASSCODE_INITIAL_LOGIN, isInitialLogin);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (isInitialScreen) {
            super.onBackPressed();
        }
    }

    private void resetPasscode() {
        resetPasscode = false;
        btnSkip.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.VISIBLE);
        tvPasscodeIntro.setText(R.string.passcode_setup);
        counter = 0;
        mifosPassCodeView.clearPasscodeField();
        mifosPassCodeView.setPassCodeListener(null);
        passcodePreferencesHelper.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (passcodePreferencesHelper.getAuthType().equalsIgnoreCase("fpauth")) {
            new FpAuthDialog(MifosPassCodeActivity.this)
                    .setTitle(getFpDialogTitle())
                    .setCallback(new FpAuthCallback() {
                        @Override
                        public void onFpAuthSuccess() {
                            startHomeActivity();
                        }

                        @Override
                        public void onCancel() {
                            cancelFingerprintAuth();
                        }
                    }).show();
        }
    }
}
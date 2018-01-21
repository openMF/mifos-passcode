package com.mifos.passcode;

import android.view.View;
import android.widget.Toast;

import com.mifos.mobile.passcode.MifosPassCodeActivity;
import com.mifos.mobile.passcode.utils.EncryptionUtil;

/**
 * Created by dilpreet on 19/01/18.
 */

public class PassCodeActivity extends MifosPassCodeActivity {

    @Override
    public int getLogo() {
        //logo to be shown on the top
        return R.drawable.mifos_logo;
    }

    @Override
    public void startNextActivity() {
        //start intent for the next activity
    }

    @Override
    public void startLoginActivity() {
        //start intent for the login activity
    }

    @Override
    public void showToaster(View view, int msg) {
        //show prompts in toast or using snackbar
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getEncryptionType() {
        return EncryptionUtil.DEFAULT;
    }

}

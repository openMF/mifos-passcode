package com.mifos.mobile.passcode;

import com.mifos.mobile.passcode.utils.ForegroundChecker;

import androidx.activity.ComponentActivity;


/**
 * Created by dilpreet on 19/01/18.
 */

public abstract class BasePassCodeActivity extends ComponentActivity implements
        ForegroundChecker.Listener {

    @Override
    protected void onResume() {
        super.onResume();
        ForegroundChecker.get().addListener(this);
        ForegroundChecker.get().onActivityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ForegroundChecker.get().onActivityPaused();
    }

    @Override
    public void onBecameForeground() {
        MifosPassCodeActivity.startMifosPassCodeActivity(this, getPassCodeClass(),
                false);
    }

    public abstract Class getPassCodeClass();

}

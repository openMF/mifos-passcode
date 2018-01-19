package com.mifos.passcode;

import android.app.Application;

import com.mifos.mobile_passcode.utils.ForegroundChecker;

/**
 * Created by dilpreet on 19/01/18.
 */

public class MifosApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //need to initialize this
        ForegroundChecker.init(this);
    }
}

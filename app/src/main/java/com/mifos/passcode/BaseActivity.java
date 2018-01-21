package com.mifos.passcode;

import com.mifos.mobile.passcode.BasePassCodeActivity;

/**
 * Created by dilpreet on 19/01/18.
 */

public class BaseActivity extends BasePassCodeActivity {

    @Override
    public Class getPassCodeClass() {
        //name of the activity which extends MifosPassCodeActivity
        return PassCodeActivity.class;
    }
}

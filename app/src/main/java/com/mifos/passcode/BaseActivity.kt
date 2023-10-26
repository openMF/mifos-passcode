package com.mifos.passcode

import com.mifos.mobile.passcode.BasePassCodeActivity

/**
 * Created by dilpreet on 19/01/18.
 */
class BaseActivity : BasePassCodeActivity() {
    override val passCodeClass: Class<*>
        get() =//name of the activity which extends MifosPassCodeActivity
            PassCodeActivity::class.java
}
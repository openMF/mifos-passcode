# mobile-passcode
Library for passcode implementation along with an optional additional feature to ask for passcode when your app resumes from background. (Works with minSDK >= 15)

## Example

<img src="https://raw.githubusercontent.com/openMF/mobile-passcode/master/graphic/demo.png" width=250 height=450/>

For a basic implementation of the PassCode Screen

```
public class PassCodeActivity extends MifosPassCodeActivity {

    @Override
    public int getLogo() {
        //logo to be shown on the top
        return R.drawable.your_logo;
    }

    @Override
    public void startNextActivity() {
        //start intent for the next activity
    }

    @Override
    public void startLoginActivity() {
        //start intent for the login or previous activity
    }

    @Override
    public void showToaster(View view, int msg) {
        //show prompts in toast or using snackbar
    }

    @Override
    public int getEncryptionType() {
        return EncryptionUtil.DEFAULT;
    }

}

```

There are 4 different types of encryption methods available with respect to our Android Projects:
 - DEFAULT
 - MOBILE_BANKING
 - ANDROID_CLIENT  
 - FINERACT_CN
 
To access the passcode stored use `PasscodePreferencesHelper`
```
PasscodePreferencesHelper pref = new PasscodePreferencesHelper(context);
pref.getPassCode();// it will return encrypted passcode according to the encryption type chosen.

```

## Asking for the passcode when your app resumes from background

Create a `BaseActivity` which should extend `BasePassCodeActivity` and extend this class for all your activities

```
public class BaseActivity extends BasePassCodeActivity {

    @Override
    public Class getPassCodeClass() {
        //name of the activity which extends MifosPassCodeActivity
        return PassCodeActivity.class;
    }
}

```

In your application class add:

```
public class MifosApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Initialize ForegroundChecker
        ForegroundChecker.init(this);
    }
}
```

For complete example click <a href="https://github.com/openMF/mobile-passcode/tree/master/app/src/main">here</a> 
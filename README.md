# mobile-passcode
Library for Passcode and Fingerprint Authentication implementation along with an optional additional
feature to ask for passcode when your app resumes from background. (Works with minSDK >= 15)

Usage
-----

In order to use the library

**1. Gradle dependency**

  -  Add the following to your project level `build.gradle`:

```gradle
allprojects {
	repositories {
		jcenter()
	}
}
```
  -  Add this to your app `build.gradle`:

```gradle
dependencies {
	implementation 'com.mifos.mobile:mifos-passcode:1.0.0'
}
```

## Example

<img src="https://raw.githubusercontent.com/openMF/mobile-passcode/master/graphic/demo.png" width=250 height=450/>

For a basic implementation of the PassCode Screen

```java
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

    @Override
    public String getFpDialogTitle() {
        //Title to be shown for Fingerprint Dialog
        return getString(R.string.fingerprint_dialog_title);
    }
}

```

## Fingerprint Authentication

 - Mention the title of Fingerprint Dialog by overriding **getFpDialogTitle()** method.

 - If the device supports Fingerprint Authentication, then a dialog will be shown to the user to enable Fingerprint
   Authentication at first time. If the user chooses "No", then he/she will be prompted to the Passcode Screen.

 - If the user opts for Fingerprint Authentication, then a dialog will appear asking user to "Touch the Sensor" in
   order to access the app.

 - On **successful** authentication, **startNextActivity()** method will be executed.

 - In case the Authentication **fails**, then after a 1 second delay, the dialog will again ask for Authentication.

 - There is also an option to **cancel** Fingerprint Authentication, if chosen results in the execution of
   **StartLoginActivity()** method.

 - This feature won't work if the Device doesn't have a fingerprint scanner or if the Android Version is below
   **Android Marshmallow (Android 6)**. In this case, the user will be directly prompted to the Passcode Screen.

There are 4 different types of encryption methods available with respect to our Android Projects:
 - DEFAULT
 - MOBILE_BANKING
 - ANDROID_CLIENT  
 - FINERACT_CN
 
To access the passcode stored use `PasscodePreferencesHelper`
```java
PasscodePreferencesHelper pref = new PasscodePreferencesHelper(context);
pref.getPassCode();// it will return encrypted passcode according to the encryption type chosen.

```

## Asking for the passcode when your app resumes from background

Create a `BaseActivity` which should extend `BasePassCodeActivity` and extend this class for all your activities

```java
public class BaseActivity extends BasePassCodeActivity {

    @Override
    public Class getPassCodeClass() {
        //name of the activity which extends MifosPassCodeActivity
        return PassCodeActivity.class;
    }
}

```

In your application class add:

```java
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

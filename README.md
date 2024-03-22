# Mobile-Passcode
Library for passcode implementation along with an optional additional feature to ask for passcode when your app resumes from background. (Works with minSDK >= 21)

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
	implementation 'com.github.openMF.mifos-passcode:compose:1.0.3'
}
```

## Example

https://github.com/openMF/mifos-passcode/assets/90026952/ee9e9610-fdb6-49d3-b485-9eb9cd78fb9f

For a basic implementation of the PassCode Screen
- Inject the `PasscodeRepository` in your activity which is essentially abstracting the operations related to saving, retrieving, and validating the passcode
- Import `PasscodeScreen` to your project which has 4 parameters mentioned below:
  - `onForgotButton`: This will allow to handle the case when the user isn't able to log into the app. In our project we are redirecting the user to login page
  - `onSkipButton`: This offers users the flexibility to bypass the passcode setup process, granting them immediate access to the desired screen
  - `onPasscodeConfirm`: This allows you to pass a function that accepts a string parameter
  - `onPasscodeRejected`: This can be used to handle the event when user has entered a wrong passcode

- This is how a typical implementation would look like

```kotlin
    @Inject
    lateinit var passcodeRepository: PasscodeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosPasscodeTheme {
                PasscodeScreen(
                    onForgotButton = {},
                    onSkipButton = {},
                    onPasscodeConfirm = {},
                    onPasscodeRejected = {}
                )
            }
        }
    }
```
- You can now define functions of your own and pass them to their respective fields. You can find the entire implementation in the `PasscodeActivity` of `:app` module

## Screenshots
- Here are some screenshots of the app
<table>
  <tr>
    <td><img src="https://github.com/openMF/mifos-passcode/assets/90026952/34cf73fd-68dc-4f6b-915b-690310238b10" width=250 height=510></td>
    <td><img src="https://github.com/openMF/mifos-passcode/assets/90026952/e01c0357-9bd2-4472-b2c1-3826be89cd8c" width=250 height=510></td>
    <td><img src="https://github.com/openMF/mifos-passcode/assets/90026952/377d83da-1956-45c6-96c1-7befbf545264" width=250 height=510></td>
  </tr>
</table>
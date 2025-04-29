# Mifos-Passcode
Library for passcode implementation along with an optional additional feature to ask for passcode when your app resumes from background. (Works with minSDK >= 21)

# 📦 Archived Repository Notice

> **⚠️ This repository is archived and no longer maintained.**

Development for **Mifos Passcode** has been **moved to a new repository** built with Kotlin Multiplatform technology.

### 🔄 New Repository

Please refer to the actively maintained repository here:  
👉 [https://github.com/openMF/mifos-passcode-cmp](https://github.com/openMF/mifos-passcode-cmp)

### 📌 Why the move?

This transition allows for:
- ✨ Modernized architecture using **Kotlin Multiplatform (KMP)**
- ✅ Shared logic across Android, iOS, and other platforms
- 🚀 Improved performance, testability, and scalability

# Project Structure :

- **`androidApp` Module**:
  - Contains the Android-specific application code.
  - Depends on the `shared` module to utilize common code across platforms.

- **`iosApp` Module**:
  - Holds the iOS-specific application code.
  - Integrates the `shared` module, typically as a framework, to access shared logic.

- **`shared` Module**:
  - The core module containing platform-agnostic code, including business logic and Compose Multiplatform UI components.
  - Referenced by both `androidApp` and `iosApp` modules to promote code reuse.

- **`cmp-mifos-passcode` Module**:
  - A specialized module designed to package and publish the `shared` module as a Compose Multiplatform (CMP) library.
  - Facilitates the distribution and reuse of the shared codebase across different projects or teams.


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

## Android Implementation:

https://github.com/user-attachments/assets/a93781d1-13b6-4c48-a2c1-2021ca56fd0b

## IOS Implementation : 

https://github.com/user-attachments/assets/9267d7ad-1e95-4212-8f5b-4516413e8b5d

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

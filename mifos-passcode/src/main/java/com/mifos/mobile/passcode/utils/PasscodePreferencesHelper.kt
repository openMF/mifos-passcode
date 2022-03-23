package com.mifos.mobile.passcode.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by dilpreet on 19/01/18.
 */

public class PasscodePreferencesHelper {

    private SharedPreferences sharedPreferences;
    private final String TOKEN = "preferences_mifos_passcode_string";
    private final String FINGERPRINTENABLER = "fingerprint_enable_dialog";
    private final String AUTHTYPE = "auth_type";

    public PasscodePreferencesHelper(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void savePassCode(String token) {
        sharedPreferences.edit().putString(TOKEN, token).apply();
    }

    public String getPassCode() {
        return sharedPreferences.getString(TOKEN, "");
    }

    public void setFingerprintEnableDialogState(boolean show) {
        sharedPreferences.edit().putBoolean(FINGERPRINTENABLER, show).apply();
    }

    public Boolean getFingerprintEnableDialogState() {
        return sharedPreferences.getBoolean(FINGERPRINTENABLER, true);
    }

    public void setAuthType(String authType) {
        sharedPreferences.edit().putString(AUTHTYPE, authType).apply();
    }

    public String getAuthType() {
        return sharedPreferences.getString(AUTHTYPE, "");
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

}

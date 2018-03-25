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

    public PasscodePreferencesHelper(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void savePassCode(String token) {
        sharedPreferences.edit().putString(TOKEN, token).apply();
    }

    public String getPassCode() {
        return sharedPreferences.getString(TOKEN, "");
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

}

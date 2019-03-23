package com.mifos.mobile.passcode.utils;

import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * Created by dilpreet on 11/7/17.
 */

public class EncryptionUtil {

    public static final int DEFAULT = 1;
    public static final int MOBILE_BANKING = 2;
    public static final int ANDROID_CLIENT = 3;
    public static final int FINERACT_CN = 4;

    @IntDef({DEFAULT, MOBILE_BANKING, ANDROID_CLIENT, FINERACT_CN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TYPE { };

    static {
        try {
            System.loadLibrary("encryption");
        } catch (UnsatisfiedLinkError e) {
            Log.e("LoadJniLib", "Error: Could not load native library: " + e.getMessage());
        }
    }

    private static final native String getPassCodeHash(String passcode);

    public static String getDefaultHash(String passCode) {
        return getPassCodeHash(passCode);
    }

    public static String getMobileBankingHash(String passCode) {
        return getPassCodeHash(passCode);
    }

    public static String getAndroidClientHash(String passCode) {
        return getPassCodeHash(passCode);
    }

    public static String getFineractCNHash(String passCode) {
        return getPassCodeHash(passCode);
    }
}

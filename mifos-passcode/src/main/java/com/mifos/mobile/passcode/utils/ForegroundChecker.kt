package com.mifos.mobile.passcode.utils

import android.content.Context
import android.os.Handler

/**
 * Created by dilpreet on 18/7/17.
 */
class ForegroundChecker private constructor(context: Context) {
    interface Listener {
        fun onBecameForeground()
    }

    /**
     * Returns True if application is in foreground
     * @return State of Application
     */
    var isForeground = false
        private set
    private var paused = true
    private val handler = Handler()
    private var listener: Listener? = null
    private var check: Runnable? = null
    private var backgroundTimeStart: Long
    private val passcodePreferencesHelper: PasscodePreferencesHelper

    /**
     * Returns True if application is in background
     * @return State of Application
     */
    val isBackground: Boolean
        get() = !isForeground

    fun addListener(listener: Listener?) {
        this.listener = listener
    }

    /**
     * It calls `onBecameForeground()` if `secondsInBackground` >=
     * `MIN_BACKGROUND_THRESHOLD`
     */
    fun onActivityResumed() {
        paused = false
        val wasBackground = !isForeground
        isForeground = true
        if (check != null) handler.removeCallbacks(check!!)
        if (wasBackground) {
            val secondsInBackground = ((System.currentTimeMillis() - backgroundTimeStart) /
                    1000).toInt()
            if (backgroundTimeStart != -1L && secondsInBackground >= MIN_BACKGROUND_THRESHOLD && listener != null && passcodePreferencesHelper.passCode!!.isNotEmpty()) {
                listener!!.onBecameForeground()
            }
        }
    }

    /**
     * It executes a Handler after `CHECK_DELAY` and then sets `foreground` to false
     */
    fun onActivityPaused() {
        paused = true
        if (check != null) handler.removeCallbacks(check!!)
        handler.postDelayed(Runnable {
            if (isForeground && paused) {
                isForeground = false
                backgroundTimeStart = System.currentTimeMillis()
            }
        }.also { check = it }, CHECK_DELAY)
    }

    companion object {
        const val CHECK_DELAY: Long = 500
        const val MIN_BACKGROUND_THRESHOLD = 60
        val TAG = ForegroundChecker::class.java.name
        private var instance: ForegroundChecker? = null

        /**
         * Used to initialize `instance` of [ForegroundChecker]
         * @param context Application Content
         * @return Instance of [ForegroundChecker]
         */
        @JvmStatic
        fun init(context: Context): ForegroundChecker? {
            if (instance == null) {
                instance = ForegroundChecker(context)
            }
            return instance
        }

        /**
         * Provides instance of [ForegroundChecker]
         * @return Instance of [ForegroundChecker]
         */
        fun get(): ForegroundChecker? {
            return instance
        }
    }

    /**
     * Initializes [ForegroundChecker]
     * @param context Application Context
     */
    init {
        backgroundTimeStart = -1
        passcodePreferencesHelper = PasscodePreferencesHelper(context)
    }
}
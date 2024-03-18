package com.mifos.compose.utility

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.mifos.compose.utility.Constants.VIBRATE_FEEDBACK_DURATION

/**
 * @author pratyush
 * @since 15/3/24
 */

object VibrationFeedback {

    @Suppress("DEPRECATION")
    fun vibrateFeedback(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).vibrate(
                CombinedVibration.createParallel(
                    VibrationEffect.createOneShot(
                        VIBRATE_FEEDBACK_DURATION,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            )
        } else {
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(
                        VibrationEffect.createOneShot(
                            VIBRATE_FEEDBACK_DURATION,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    it.vibrate(VIBRATE_FEEDBACK_DURATION)
                }
            }
        }
    }
}
package com.fahim.mad_lab_compose.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import android.widget.Toast
import com.fahim.mad_lab_compose.R
import kotlin.math.roundToInt

/**
 * Listens for [Intent.ACTION_BATTERY_CHANGED] and shows the current charge as a Toast.
 *
 * `ACTION_BATTERY_CHANGED` is sticky, so a Toast appears as soon as the receiver is registered.
 */
class BatteryLevelReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, MISSING_EXTRA)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, MISSING_EXTRA)
        val message = batteryPercent(level, scale)
            ?.let { context.getString(R.string.toast_battery_level, it) }
            ?: context.getString(R.string.toast_battery_unknown)
        Log.d(TAG, message)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "BatteryLevelReceiver"
        private const val MISSING_EXTRA = -1

        /** Whole-number charge percentage, or `null` when the extras are missing or invalid. */
        fun batteryPercent(level: Int, scale: Int): Int? {
            if (level < 0 || scale <= 0) return null
            return (level.toFloat() / scale * 100).roundToInt()
        }
    }
}

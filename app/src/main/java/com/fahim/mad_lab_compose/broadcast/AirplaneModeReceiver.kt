package com.fahim.mad_lab_compose.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.fahim.mad_lab_compose.R

/** Listens for [Intent.ACTION_AIRPLANE_MODE_CHANGED] and reports the new state as a Toast. */
class AirplaneModeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val isAirplaneModeOn = intent.getBooleanExtra(EXTRA_STATE, false)
        val message = if (isAirplaneModeOn) {
            R.string.toast_airplane_enabled
        } else {
            R.string.toast_airplane_disabled
        }
        Log.d(TAG, context.getString(message))
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private companion object {
        const val TAG = "AirplaneModeReceiver"


        /** Extra carried by the airplane-mode broadcast; the platform exposes no constant for it. */
        const val EXTRA_STATE = "state"
    }
}

package com.fahim.mad_lab_compose.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import com.fahim.mad_lab_compose.R

/**
 * Listens for connectivity changes and reports whether the device currently has internet.
 *
 * [ACTION_CONNECTIVITY_CHANGE] is only delivered to receivers registered at runtime, so this
 * receiver is registered from the UI rather than from the manifest.
 */
class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val message = if (hasInternet(context)) {
            R.string.toast_internet_connected
        } else {
            R.string.toast_internet_disconnected
        }
        Log.d(TAG, context.getString(message))
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hasInternet(context: Context): Boolean {
        val manager = context.getSystemService(ConnectivityManager::class.java) ?: return false
        val network = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    companion object {
        private const val TAG = "ConnectivityReceiver"

        /** Deprecated as a manifest broadcast but still delivered to runtime receivers. */
        @Suppress("DEPRECATION")
        const val ACTION_CONNECTIVITY_CHANGE = ConnectivityManager.CONNECTIVITY_ACTION
    }
}

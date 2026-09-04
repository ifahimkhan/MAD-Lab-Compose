package com.fahim.mad_lab_compose.broadcast

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

private const val TAG = "ReceiverRegistration"

/**
 * Keeps [receiver] registered for [filter] for as long as [enabled] is true and this composable
 * stays in the composition.
 *
 * Toggling [enabled] off, or leaving the screen, unregisters the receiver. This replaces the
 * View-based pattern of pairing `registerReceiver` in a listener with `unregisterReceiver` in
 * `onDestroy`.
 */
@Composable
fun ReceiverRegistration(enabled: Boolean, receiver: BroadcastReceiver, filter: IntentFilter) {
    val context = LocalContext.current
    DisposableEffect(enabled, receiver, filter) {
        if (enabled) {
            ContextCompat.registerReceiver(
                context,
                receiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
        }
        onDispose {
            if (enabled) {
                runCatching { context.unregisterReceiver(receiver) }
                    .onFailure { Log.w(TAG, "Receiver was not registered: $receiver", it) }
            }
        }
    }
}

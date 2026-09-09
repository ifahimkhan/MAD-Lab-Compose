package com.fahim.mad_lab_compose

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.util.Log

/**
 * Logs the Dalvik heap budget the process was given at launch.
 *
 * Without `android:largeHeap="true"` the hard limit is `dalvik.vm.heapgrowthlimit`, which is
 * what [Runtime.maxMemory] and [ActivityManager.getMemoryClass] report. `largeMemoryClass` is
 * logged alongside for comparison only; it is not in effect for this process.
 */
object HeapMemoryLogger {

    private const val TAG = "HeapMemory"
    private const val BYTES_PER_MB = 1024L * 1024L

    fun log(context: Context, phase: String) {
        val runtime = Runtime.getRuntime()
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val maxMb = runtime.maxMemory() / BYTES_PER_MB
        val totalMb = runtime.totalMemory() / BYTES_PER_MB
        val freeMb = runtime.freeMemory() / BYTES_PER_MB
        val usedMb = totalMb - freeMb
        val nativeAllocatedMb = Debug.getNativeHeapAllocatedSize() / BYTES_PER_MB

        Log.i(TAG, "[$phase] largeHeap flag in manifest: false")
        Log.i(TAG, "[$phase] memoryClass (per-app limit)      = ${activityManager.memoryClass} MB")
        Log.i(
            TAG,
            "[$phase] largeMemoryClass (if largeHeap) = ${activityManager.largeMemoryClass} MB"
        )
        Log.i(TAG, "[$phase] Runtime.maxMemory   (hard cap)  = $maxMb MB")
        Log.i(TAG, "[$phase] Runtime.totalMemory (allocated) = $totalMb MB")
        Log.i(TAG, "[$phase] Runtime.freeMemory              = $freeMb MB")
        Log.i(TAG, "[$phase] Java heap in use                = $usedMb MB")
        Log.i(TAG, "[$phase] Native heap allocated           = $nativeAllocatedMb MB")
    }
}

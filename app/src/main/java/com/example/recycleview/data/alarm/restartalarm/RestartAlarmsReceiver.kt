package com.example.recycleview.data.alarm.restartalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class RestartAlarmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("TAG", "onReceive: Restart receiver")
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {

            if (context != null) {
                try {
                    val restartAlarmsWorkRequest =
                        OneTimeWorkRequestBuilder<RestartAlarmsWorker>()
                            .build()
                    WorkManager.getInstance(context).enqueue(restartAlarmsWorkRequest)
                } catch (e: Exception) {
                    Log.e("TAG", "onReceive exception: ${e.message}")
                }
            }
        }
    }
}
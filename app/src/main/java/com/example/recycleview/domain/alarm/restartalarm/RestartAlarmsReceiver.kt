package com.example.recycleview.domain.alarm.restartalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestartAlarmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("TAG", "onReceive: Restart receiver")
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {

            try {
                val restartAlarmsWorkRequest = OneTimeWorkRequestBuilder<RestartAlarmsWorker>()
                    .build()
                if (context != null) {
                    WorkManager.getInstance(context).enqueue(restartAlarmsWorkRequest)
                }
            } catch (e: Exception) {
                Log.e("TAG", "onReceive exception: ${e.message}")
            }
        }
    }
}
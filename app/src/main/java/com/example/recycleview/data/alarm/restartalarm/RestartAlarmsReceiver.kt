package com.example.recycleview.data.alarm.restartalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestartAlarmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("TAG", "onReceive: Restart receiver")
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {

            try {
                val intentRestartAlarms = Intent(context, RestartAlarmsService::class.java)

                if (context != null) {
                    RestartAlarmsService.enqueueWork(context, intentRestartAlarms)
                }
            } catch (e: Exception) {
                Log.e("TAG", "onReceive exception: ${e.message}")
            }
        }
    }
}
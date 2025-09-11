package com.example.recycleview.di.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

interface ChildWorkerFactory {
    fun create(appContext: Context, params: WorkerParameters): CoroutineWorker
}
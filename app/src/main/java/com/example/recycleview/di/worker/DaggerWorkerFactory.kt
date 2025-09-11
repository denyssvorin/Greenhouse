package com.example.recycleview.di.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider

class DaggerWorkerFactory @Inject constructor(
    private val workersFactories: @JvmSuppressWildcards Map<Class<out CoroutineWorker>, Provider<ChildWorkerFactory>>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val workerClass = try {
            Class.forName(workerClassName).asSubclass(CoroutineWorker::class.java)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            return null
        }

        val factoryProvider = workersFactories[workerClass] ?: return null
        return factoryProvider.get().create(appContext, workerParameters)
    }
}
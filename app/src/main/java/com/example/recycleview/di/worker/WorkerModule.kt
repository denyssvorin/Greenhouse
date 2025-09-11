package com.example.recycleview.di.worker

import androidx.work.CoroutineWorker
import com.example.recycleview.data.scheduler.alarm.restart.RestartAlarmsWorker
import com.example.recycleview.data.scheduler.notification.NotificationWorker
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Module
interface WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(RestartAlarmsWorker::class)
    fun bindRestartAlarmsWorkerFactory(factory: RestartAlarmsWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(NotificationWorker::class)
    fun bindNotificationWorkerFactory(factory: NotificationWorker.Factory): ChildWorkerFactory

}

@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class WorkerKey(val value: KClass<out CoroutineWorker>)
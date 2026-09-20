package com.tesis_pro.tenderapp.data.notification

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.tesis_pro.tenderapp.data.auth.SecureAuthSessionStore
import java.util.concurrent.TimeUnit

class PushRegistrationWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val registration = StoredSessionPushRegistration(
            sessionStore = SecureAuthSessionStore(applicationContext),
            postLoginPushRegistrar = AndroidPostLoginPushRegistrar(
                tokenProvider = FirebasePushTokenProvider(
                    tokenStore = FirebasePushTokenStore(applicationContext),
                ),
            ),
        )

        return when (registration.registerIfSessionExists()) {
            null -> Result.success()
            PushRegistrationResult.Registered -> Result.success()
            is PushRegistrationResult.Skipped -> Result.success()
            is PushRegistrationResult.Failed -> Result.retry()
        }
    }
}

object PushRegistrationWorkScheduler {
    fun enqueue(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = OneTimeWorkRequestBuilder<PushRegistrationWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                BACKOFF_DELAY_MINUTES,
                TimeUnit.MINUTES,
            )
            .addTag(TAG)
            .build()

        WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    private const val UNIQUE_WORK_NAME = "push-registration-retry"
    private const val TAG = "TenderAppPushRegistration"
    private const val BACKOFF_DELAY_MINUTES = 15L
}

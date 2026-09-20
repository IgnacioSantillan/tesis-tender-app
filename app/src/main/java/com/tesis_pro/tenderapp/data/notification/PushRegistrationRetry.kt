package com.tesis_pro.tenderapp.data.notification

import android.content.Context
import com.tesis_pro.tenderapp.domain.model.AuthSession

interface PushRegistrationRetryScheduler {
    fun enqueueRetry()
}

object NoOpPushRegistrationRetryScheduler : PushRegistrationRetryScheduler {
    override fun enqueueRetry() = Unit
}

class WorkManagerPushRegistrationRetryScheduler(
    context: Context,
) : PushRegistrationRetryScheduler {
    private val appContext = context.applicationContext

    override fun enqueueRetry() {
        PushRegistrationWorkScheduler.enqueue(appContext)
    }
}

class RetryingPostLoginPushRegistrar(
    private val delegate: PostLoginPushRegistrar,
    private val retryScheduler: PushRegistrationRetryScheduler,
) : PostLoginPushRegistrar {
    override suspend fun registerAfterLogin(session: AuthSession): PushRegistrationResult {
        val result = delegate.registerAfterLogin(session)
        if (PushRegistrationRetryPolicy.shouldEnqueueRetry(result)) {
            retryScheduler.enqueueRetry()
        }
        return result
    }
}

object PushRegistrationRetryPolicy {
    fun shouldEnqueueRetry(result: PushRegistrationResult): Boolean {
        return result is PushRegistrationResult.Failed
    }
}

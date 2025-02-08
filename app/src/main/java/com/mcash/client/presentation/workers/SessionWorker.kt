package com.mcash.client.presentation.workers

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.usecases.user.ClearUserUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class SessionWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workParams: WorkerParameters,
    private val preferences: PreferenceRepository,
    val clearUserUseCase: ClearUserUseCase
) : CoroutineWorker(context, workParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            preferences.saveSignOutState()
            clearUserUseCase()
            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "session worker failed with exception: ${e.message} ")
            Result.failure()
        }

    }
}
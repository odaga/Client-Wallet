package com.mcash.client.presentation.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mcash.client.BuildConfig
import com.mcash.domain.dispacher.AppDispatcher
import com.mcash.domain.usecases.account.FetchAccountBalanceUseCase
import com.mcash.domain.usecases.nwsc.FetchNwscAresUseCase
import com.mcash.domain.usecases.settings.FAQUseCase
import com.mcash.domain.usecases.settings.HelpUseCase
import com.mcash.domain.usecases.utility.GetPackagesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class AppSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParameters: WorkerParameters,
    private val fetchAccountBalanceUseCase: FetchAccountBalanceUseCase,
    private val fetchNwscAresUseCase: FetchNwscAresUseCase,
    private val getPackagesUseCase: GetPackagesUseCase,
    private val faqUseCase: FAQUseCase,
    private val helpUseCase: HelpUseCase,
    private val dispatcher: AppDispatcher
) : CoroutineWorker(context, workerParameters) {

    companion object {
        const val WORK_NAME = "${BuildConfig.APPLICATION_ID}AppSyncWorker"
    }

    override suspend fun doWork(): Result = withContext(dispatcher.io) {
        Timber.d("Running App Sync...")
        try {
            fetchAccountBalanceUseCase()
            faqUseCase()
            fetchNwscAresUseCase()
            getPackagesUseCase.getDataPackages()
            getPackagesUseCase.getVoicePackages()
            getPackagesUseCase.getTvPackages()
           // helpUseCase()
            Result.success()
        } catch (ex: Exception) {
            Log.d("Exception", "$ex")
            Result.failure()
        }
    }
}
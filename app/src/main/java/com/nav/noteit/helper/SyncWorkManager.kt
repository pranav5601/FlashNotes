package com.nav.noteit.helper

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.CoroutineWorker
import androidx.work.Operation
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class SyncWorkManager(val context: Context, val workerParams: WorkerParameters): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        delay(1000);
        Log.e("working worker", "working worker")

        return Result.success()
    }

}
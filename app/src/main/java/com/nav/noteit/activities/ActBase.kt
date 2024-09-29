package com.nav.noteit.activities

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.nav.noteit.helper.Loader
import com.nav.noteit.helper.SyncWorkManager
import java.util.concurrent.TimeUnit


abstract class ActBase : AppCompatActivity() {

    private lateinit var workManager: WorkManager
    private var loader: Loader? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)


        Log.e("package", this.localClassName)

    }

    fun callWorkRequest() {

        val workRequest = PeriodicWorkRequestBuilder<SyncWorkManager>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS,
            flexTimeInterval = 15,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        ).build()
        workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueueUniquePeriodicWork(
            "sync_data",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
        workManager.getWorkInfosForUniqueWorkLiveData("sync_data").observe(this) {
            it.forEach { workInfo ->
                Log.e("work_info", workInfo.state.name)
            }
        }
    }


    fun showLoader() {

        if (loader == null)

            loader = Loader(this)

        loader?.show()
    }

    fun closeLoader() {
        loader?.dismiss()
    }





}
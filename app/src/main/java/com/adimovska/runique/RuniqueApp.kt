package com.adimovska.runique

import android.app.Application
import android.content.Context
import com.adimovska.auth.data.di.authDataModule
import com.adimovska.auth.presentation.di.authViewModelModule
import com.adimovska.core.data.di.coreDataModule
import com.adimovska.core.database.di.databaseModule
import com.adimovska.run.location.di.locationModule
import com.adimovska.run.network.di.networkModule
import com.adimovska.run.presentation.di.runPresentationModule
import com.adimovska.runique.di.appModule
import com.google.android.play.core.splitcompat.SplitCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import runDataModule
import timber.log.Timber

class RuniqueApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())
    // supervisor job means that each coroutine we launch cannot be independent, if one fails - all fail

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RuniqueApp)
            workManagerFactory()
            modules(
                authDataModule,
                authViewModelModule,
                appModule,
                coreDataModule,
                runPresentationModule,
                locationModule,
                databaseModule,
                networkModule,
                runDataModule,
            )
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }
}
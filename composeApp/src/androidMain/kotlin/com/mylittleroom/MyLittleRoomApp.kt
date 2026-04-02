package com.mylittleroom

import android.app.Application
import com.mylittleroom.di.appModule
import com.mylittleroom.di.platformModule
import com.mylittleroom.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyLittleRoomApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyLittleRoomApp)
            modules(platformModule, sharedModule, appModule)
        }
    }
}

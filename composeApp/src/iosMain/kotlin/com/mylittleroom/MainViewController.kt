package com.mylittleroom

import androidx.compose.ui.window.ComposeUIViewController
import com.mylittleroom.di.appModule
import com.mylittleroom.di.platformModule
import com.mylittleroom.di.sharedModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(platformModule, sharedModule, appModule)
    }
}

fun MainViewController() = ComposeUIViewController { App() }

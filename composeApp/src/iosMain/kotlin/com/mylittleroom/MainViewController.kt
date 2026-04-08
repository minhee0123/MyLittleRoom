package com.mylittleroom

import androidx.compose.ui.window.ComposeUIViewController
import com.mylittleroom.di.appModule
import com.mylittleroom.di.platformModule
import com.mylittleroom.di.sharedModule
import org.koin.core.context.startKoin

/** iOS 앱 시작 시 Swift에서 호출하여 Koin DI를 초기화한다. */
fun initKoin() {
    startKoin {
        modules(platformModule, sharedModule, appModule)
    }
}

/** iOS용 ComposeUIViewController 생성 — SwiftUI에서 UIViewControllerRepresentable로 사용 */
fun MainViewController() = ComposeUIViewController { App() }

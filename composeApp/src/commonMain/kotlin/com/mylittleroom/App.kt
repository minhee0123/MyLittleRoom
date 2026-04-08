package com.mylittleroom

import androidx.compose.runtime.Composable
import com.mylittleroom.designsystem.MyLittleRoomTheme
import com.mylittleroom.ui.navigation.AppNavigation

/** 앱 루트 Composable — 테마 적용 후 내비게이션 시작점 */
@Composable
fun App() {
    MyLittleRoomTheme {
        AppNavigation()
    }
}

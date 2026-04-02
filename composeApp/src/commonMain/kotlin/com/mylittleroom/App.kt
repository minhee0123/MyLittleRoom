package com.mylittleroom

import androidx.compose.runtime.Composable
import com.mylittleroom.designsystem.MyLittleRoomTheme
import com.mylittleroom.ui.navigation.AppNavigation

@Composable
fun App() {
    MyLittleRoomTheme {
        AppNavigation()
    }
}

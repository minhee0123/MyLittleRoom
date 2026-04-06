package com.mylittleroom.platform

actual suspend fun syncWidgets() {
    // iOS WidgetKit reload is handled via WidgetCenter in Swift
    // Will be connected when Xcode widget extension target is set up
}

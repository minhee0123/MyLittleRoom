package com.mylittleroom.di

import com.mylittleroom.data.AndroidDatabaseFactory
import com.mylittleroom.data.DatabaseFactory
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseFactory> { AndroidDatabaseFactory(get()) }
}

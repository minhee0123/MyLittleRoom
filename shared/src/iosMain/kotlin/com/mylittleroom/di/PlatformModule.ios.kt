package com.mylittleroom.di

import com.mylittleroom.data.DatabaseFactory
import com.mylittleroom.data.IosDatabaseFactory
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseFactory> { IosDatabaseFactory() }
}

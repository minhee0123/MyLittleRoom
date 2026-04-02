package com.mylittleroom.di

import com.mylittleroom.data.AppDatabase
import com.mylittleroom.data.buildAppDatabase
import com.mylittleroom.data.repository.FurnitureRepository
import com.mylittleroom.data.repository.HabitRepository
import com.mylittleroom.data.repository.UserRepository
import org.koin.dsl.module

val sharedModule = module {
    single<AppDatabase> { buildAppDatabase(get()) }
    single { get<AppDatabase>().habitDao() }
    single { get<AppDatabase>().habitLogDao() }
    single { get<AppDatabase>().furnitureDao() }
    single { get<AppDatabase>().userStatusDao() }

    single { HabitRepository(get(), get()) }
    single { UserRepository(get()) }
    single { FurnitureRepository(get()) }
}

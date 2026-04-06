package com.mylittleroom.di

import com.mylittleroom.ui.viewmodel.CharacterRoomViewModel
import com.mylittleroom.ui.viewmodel.FurniturePlacementViewModel
import com.mylittleroom.ui.viewmodel.HabitListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::CharacterRoomViewModel)
    viewModelOf(::HabitListViewModel)
    viewModelOf(::FurniturePlacementViewModel)
}

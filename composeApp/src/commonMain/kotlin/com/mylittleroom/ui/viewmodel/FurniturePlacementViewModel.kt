package com.mylittleroom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mylittleroom.data.entity.FurnitureEntity
import com.mylittleroom.data.repository.FurnitureRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FurniturePlacementUiState(
    val unlockedFurniture: List<FurnitureEntity> = emptyList(),
    val placedFurniture: List<FurnitureEntity> = emptyList()
)

class FurniturePlacementViewModel(
    private val furnitureRepository: FurnitureRepository
) : ViewModel() {

    val uiState: StateFlow<FurniturePlacementUiState> = combine(
        furnitureRepository.getUnlockedFurniture(),
        furnitureRepository.getPlacedFurniture()
    ) { unlocked, placed ->
        FurniturePlacementUiState(
            unlockedFurniture = unlocked,
            placedFurniture = placed
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FurniturePlacementUiState())

    fun placeFurniture(furnitureId: String, slot: String) {
        viewModelScope.launch {
            furnitureRepository.placeFurniture(furnitureId, slot)
        }
    }
}

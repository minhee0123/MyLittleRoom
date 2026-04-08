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

/** 가구 배치 화면의 UI 상태 */
data class FurniturePlacementUiState(
    val unlockedFurniture: List<FurnitureEntity> = emptyList(),
    val placedFurniture: List<FurnitureEntity> = emptyList()
)

/** 가구 배치 ViewModel — 해금된 가구 목록 관찰 및 슬롯 배치 액션 */
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

    /** 선택한 슬롯에 가구를 배치한다 (기존 가구는 자동 제거). */
    fun placeFurniture(furnitureId: String, slot: String) {
        viewModelScope.launch {
            furnitureRepository.placeFurniture(furnitureId, slot)
        }
    }
}

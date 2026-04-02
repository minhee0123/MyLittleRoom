package com.mylittleroom.ui.screen

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mylittleroom.domain.model.CharacterStage
import com.mylittleroom.ui.viewmodel.CharacterRoomUiState
import com.mylittleroom.ui.viewmodel.CharacterRoomViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CharacterRoomScreen(
    modifier: Modifier = Modifier,
    viewModel: CharacterRoomViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatusBar(
            level = uiState.level,
            currentExp = uiState.currentExp,
            maxExp = uiState.maxExp,
            characterStage = uiState.characterStage
        )

        Spacer(modifier = Modifier.height(24.dp))

        RoomArea(
            characterStage = uiState.characterStage,
            allCompleted = uiState.todayHabitsTotal > 0 &&
                    uiState.todayHabitsCompleted == uiState.todayHabitsTotal
        )

        Spacer(modifier = Modifier.height(24.dp))

        FurnitureSlots(uiState = uiState)

        Spacer(modifier = Modifier.weight(1f))

        TodayHabitsSummary(
            completed = uiState.todayHabitsCompleted,
            total = uiState.todayHabitsTotal
        )
    }
}

@Composable
private fun StatusBar(level: Int, currentExp: Int, maxExp: Int, characterStage: CharacterStage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Lv.$level",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = characterStage.stageName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = characterStage.emoji,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { if (maxExp > 0) currentExp / maxExp.toFloat() else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surface,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$currentExp / $maxExp EXP",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun RoomArea(characterStage: CharacterStage, allCompleted: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Animated character
                AnimatedCharacter(
                    emoji = characterStage.emoji,
                    isHappy = allCompleted
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = characterStage.stageName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (allCompleted) "오늘의 습관을 모두 완료했어요!" else characterStage.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (allCompleted)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun AnimatedCharacter(emoji: String, isHappy: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()

    // Floating bounce animation
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isHappy) -12f else -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isHappy) 600 else 1200),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Scale pulse when happy
    val scale by animateFloatAsState(
        targetValue = if (isHappy) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 200f)
    )

    Text(
        text = emoji,
        fontSize = 72.sp,
        modifier = Modifier
            .offset { IntOffset(0, offsetY.toInt()) }
            .scale(scale)
    )
}

@Composable
private fun FurnitureSlots(uiState: CharacterRoomUiState) {
    val slotDefinitions = listOf(
        Pair("wall", Pair("\uD83D\uDECB\uFE0F", "소파")),
        Pair("wall2", Pair("\uD83D\uDDBC\uFE0F", "액자")),
        Pair("floor", Pair("\uD83C\uDF3F", "화분")),
        Pair("desk", Pair("\uD83D\uDCA1", "조명"))
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        slotDefinitions.forEach { (slot, emojiLabel) ->
            val (defaultEmoji, label) = emojiLabel
            val placed = uiState.placedFurniture.find { it.slotPosition == slot }
            FurnitureSlot(
                emoji = defaultEmoji,
                label = if (placed != null) placed.name else label,
                isOccupied = placed != null
            )
        }
    }
}

@Composable
private fun FurnitureSlot(emoji: String, label: String, isOccupied: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isOccupied)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.tertiaryContainer
                )
                .border(
                    width = 1.5.dp,
                    color = if (isOccupied)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    else
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 28.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun TodayHabitsSummary(completed: Int, total: Int) {
    val allDone = completed == total && total > 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (allDone)
                MaterialTheme.colorScheme.tertiaryContainer
            else
                MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (allDone) "오늘의 습관 완료!" else "오늘의 습관",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (allDone)
                        MaterialTheme.colorScheme.onTertiaryContainer
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (total == 0) "등록된 습관이 없어요"
                    else if (allDone) "대단해요! 오늘도 갓생 완료 ✨"
                    else "$completed / $total 완료",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (allDone)
                        MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }

            if (total > 0) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (allDone)
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.secondary
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (allDone) "✨" else "$completed/$total",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

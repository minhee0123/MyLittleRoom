package com.mylittleroom.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mylittleroom.domain.RewardEvent
import com.mylittleroom.ui.component.RewardDialog
import com.mylittleroom.ui.viewmodel.HabitListViewModel
import com.mylittleroom.ui.viewmodel.HabitWithStatus
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HabitListScreen(
    onAddHabit: () -> Unit,
    viewModel: HabitListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentReward by remember { mutableStateOf<RewardEvent?>(null) }

    LaunchedEffect(Unit) {
        viewModel.rewardEvents.collect { event ->
            currentReward = event
        }
    }

    currentReward?.let { event ->
        RewardDialog(
            event = event,
            onDismiss = { currentReward = null }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabit,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "습관 추가")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "오늘의 습관",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            val completed = uiState.habits.count { it.isCompletedToday }
            Text(
                text = "${completed}/${uiState.habits.size} 완료",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.habits.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📝", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "아직 등록된 습관이 없어요",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+ 버튼을 눌러 습관을 추가해보세요!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.habits, key = { it.habit.id }) { habitWithStatus ->
                        HabitCard(
                            habitWithStatus = habitWithStatus,
                            onToggle = { viewModel.toggleHabitCompletion(habitWithStatus.habit.id) },
                            onDelete = { viewModel.deleteHabit(habitWithStatus.habit) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun HabitCard(
    habitWithStatus: HabitWithStatus,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val containerColor by animateColorAsState(
        if (habitWithStatus.isCompletedToday)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant
    )

    // Scale-up animation on press
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = 500f)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onToggle
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = habitWithStatus.habit.emoji, fontSize = 32.sp)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habitWithStatus.habit.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (habitWithStatus.isCompletedToday)
                        TextDecoration.LineThrough else TextDecoration.None
                )
                if (habitWithStatus.streak > 0) {
                    Text(
                        text = "🔥 ${habitWithStatus.streak}일 연속",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (habitWithStatus.isCompletedToday)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (habitWithStatus.isCompletedToday) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "완료",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "삭제",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

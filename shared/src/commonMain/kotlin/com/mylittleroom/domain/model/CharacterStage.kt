package com.mylittleroom.domain.model

enum class CharacterStage(
    val stageName: String,
    val emoji: String,
    val description: String,
    val minLevel: Int
) {
    STAR_DUST("별먼지", "✨", "작고 반짝이는 먼지", 1),
    SMALL_STAR("작은 별", "⭐", "호기심 많은 작은 별", 5),
    BRIGHT_STAR("반짝 별", "🌟", "빛나는 별", 10),
    BIG_STAR("큰 별", "💫", "악세서리를 착용한 큰 별", 20),
    CONSTELLATION("별자리", "🌌", "마법 이펙트의 별자리", 35);

    companion object {
        fun fromLevel(level: Int): CharacterStage = entries
            .sortedByDescending { it.minLevel }
            .first { level >= it.minLevel }
    }
}

package ir.danialchoopan.lumalogic.domain.model

import ir.danialchoopan.lumalogic.domain.score.ScoreResult

/**
 * Data class representing the full result breakdown when a level attempt finishes.
 */
data class GameCompletionResult(
    val levelId: String,
    val levelName: String,
    val isWin: Boolean,
    val scoreResult: ScoreResult,
    val stars: Int,
    val energyState: EnergyState,
    val movesCount: Int,
    val timeSeconds: Long,
    val hintsUsed: Int,
    val optionalTargetsActivated: Int = 0,
    val totalOptionalTargets: Int = 0
)

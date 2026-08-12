package ir.danialchoopan.lumalogic.domain.score

/**
 * Breakdown of final level completion score calculations.
 */
data class ScoreResult(
    val totalScore: Int,
    val energyBonus: Int,
    val moveBonus: Int,
    val timeBonus: Int,
    val hintPenalty: Int,
    val optionalTargetBonus: Int,
    val baseCompletionScore: Int = 1000
)

package ir.danialchoopan.lumalogic.domain.score

import ir.danialchoopan.lumalogic.data.model.Level

/**
 * Deterministic calculator for player score and star ratings upon level completion.
 */
object ScoreCalculator {

    fun calculateScore(
        level: Level,
        levelCompleted: Boolean,
        energyRemaining: Int,
        movesCount: Int,
        timeSeconds: Long,
        hintsUsed: Int,
        optionalTargetsActivated: Int = 0
    ): ScoreResult {
        if (!levelCompleted) {
            return ScoreResult(
                totalScore = 0,
                energyBonus = 0,
                moveBonus = 0,
                timeBonus = 0,
                hintPenalty = 0,
                optionalTargetBonus = 0,
                baseCompletionScore = 0
            )
        }

        val baseCompletionScore = 1000
        val energyBonus = (energyRemaining * 20).coerceAtLeast(0)
        
        val expectedMoves = level.expectedMoves.coerceAtLeast(1)
        val moveDifference = expectedMoves - movesCount
        val moveBonus = if (moveDifference >= 0) {
            moveDifference * 50 + 200
        } else {
            (200 + moveDifference * 20).coerceAtLeast(0)
        }

        val timeBonus = (300 - timeSeconds.toInt()).coerceAtLeast(0) * 2
        val hintPenalty = (hintsUsed * 150).coerceAtLeast(0)
        val optionalTargetBonus = (optionalTargetsActivated * 250).coerceAtLeast(0)

        val rawTotal = baseCompletionScore + energyBonus + moveBonus + timeBonus + optionalTargetBonus - hintPenalty
        val totalScore = rawTotal.coerceAtLeast(100)

        return ScoreResult(
            totalScore = totalScore,
            energyBonus = energyBonus,
            moveBonus = moveBonus,
            timeBonus = timeBonus,
            hintPenalty = hintPenalty,
            optionalTargetBonus = optionalTargetBonus,
            baseCompletionScore = baseCompletionScore
        )
    }

    fun calculateStars(level: Level, scoreResult: ScoreResult, levelCompleted: Boolean): Int {
        if (!levelCompleted) return 0
        val score = scoreResult.totalScore
        return when {
            score >= level.threeStarThreshold -> 3
            score >= level.twoStarThreshold -> 2
            else -> 1
        }
    }
}

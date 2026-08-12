package ir.danialchoopan.lumalogic.domain.model

/**
 * Immutable state representing level energy consumption and depletion status.
 */
data class EnergyState(
    val maximum: Int = 50,
    val remaining: Int = 50,
    val used: Int = 0
) {
    val isDepleted: Boolean get() = remaining <= 0 || (maximum > 0 && used >= maximum)
    val isLow: Boolean get() = !isDepleted && remaining <= (maximum * 0.25f).coerceAtLeast(5f).toInt()
}

package ir.danialchoopan.lumalogic.domain.level

/**
 * Result of level structure validation.
 *
 * @property isValid True if there are no errors (level can be saved/played)
 * @property errors Critical errors that block saving or playing
 * @property warnings Non-blocking suggestions or warnings
 */
data class LevelValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList()
) {
    companion object {
        fun valid(warnings: List<String> = emptyList()): LevelValidationResult {
            return LevelValidationResult(isValid = true, errors = emptyList(), warnings = warnings)
        }

        fun invalid(errors: List<String>, warnings: List<String> = emptyList()): LevelValidationResult {
            return LevelValidationResult(isValid = false, errors = errors, warnings = warnings)
        }
    }
}

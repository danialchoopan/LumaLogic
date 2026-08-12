package ir.danialchoopan.lumalogic.domain.level

import ir.danialchoopan.lumalogic.data.model.Level
import ir.danialchoopan.lumalogic.data.repository.LevelRepository
import ir.danialchoopan.lumalogic.data.serialization.LevelJsonSerializer

/**
 * Domain service managing level operations, validation, storage, import, and export.
 */
class LevelManager(
    private val levelRepository: LevelRepository,
    private val validator: LevelValidator = LevelValidator(),
    private val serializer: LevelJsonSerializer = LevelJsonSerializer(validator)
) {

    fun getBuiltInLevels(): List<Level> {
        return levelRepository.getBuiltInLevels()
    }

    fun getUserLevels(): List<Level> {
        return levelRepository.getUserLevels()
    }

    fun getAllLevels(): List<Level> {
        return levelRepository.getLevels()
    }

    fun getLevelById(id: String): Level? {
        return levelRepository.getLevelById(id)
    }

    fun saveUserLevel(level: Level): LevelValidationResult {
        val userLevel = if (level.levelId.isBlank() || !level.levelId.startsWith("user_")) {
            level.copy(levelId = generateUniqueUserLevelId(), isUserCreated = true)
        } else {
            level.copy(isUserCreated = true)
        }

        val validationResult = validator.validate(userLevel)
        if (validationResult.isValid) {
            levelRepository.saveUserLevel(userLevel)
        }
        return validationResult
    }

    fun deleteUserLevel(id: String): Boolean {
        return levelRepository.deleteUserLevel(id)
    }

    fun importLevel(json: String): Pair<Level?, LevelValidationResult> {
        val validationResult = serializer.validateJson(json)
        if (!validationResult.isValid) {
            return Pair(null, validationResult)
        }

        return try {
            val deserialized = serializer.deserialize(json)
            val levelToSave = deserialized.copy(
                levelId = generateUniqueUserLevelId(),
                isUserCreated = true
            )
            levelRepository.saveUserLevel(levelToSave)
            Pair(levelToSave, validationResult)
        } catch (e: Exception) {
            Pair(null, LevelValidationResult.invalid(listOf("Failed to process imported level: ${e.message}")))
        }
    }

    fun exportLevel(level: Level): String {
        return serializer.serialize(level)
    }

    fun validateLevel(level: Level): LevelValidationResult {
        return validator.validate(level)
    }

    fun generateUniqueUserLevelId(): String {
        return "user_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}

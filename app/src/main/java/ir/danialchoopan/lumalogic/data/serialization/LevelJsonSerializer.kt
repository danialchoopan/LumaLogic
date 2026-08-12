package ir.danialchoopan.lumalogic.data.serialization

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import ir.danialchoopan.lumalogic.data.model.Level
import ir.danialchoopan.lumalogic.domain.level.LevelValidationResult
import ir.danialchoopan.lumalogic.domain.level.LevelValidator

/**
 * Handles JSON serialization, deserialization, and validation for Level objects.
 */
class LevelJsonSerializer(
    private val validator: LevelValidator = LevelValidator()
) {

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val adapter = moshi.adapter(Level::class.java).indent("  ")

    fun serialize(level: Level): String {
        return adapter.toJson(level)
    }

    fun deserialize(json: String): Level {
        val trimmed = json.trim()
        if (trimmed.isEmpty()) {
            throw IllegalArgumentException("JSON string is empty.")
        }
        val level = adapter.fromJson(trimmed)
            ?: throw IllegalArgumentException("Failed to deserialize JSON into Level object.")
        return level
    }

    fun validateJson(json: String): LevelValidationResult {
        return try {
            val level = deserialize(json)
            validator.validate(level)
        } catch (e: Exception) {
            LevelValidationResult.invalid(listOf("Malformed or invalid JSON: ${e.message}"))
        }
    }
}

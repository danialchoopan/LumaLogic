package ir.danialchoopan.lumalogic.data.repository

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import ir.danialchoopan.lumalogic.data.model.LevelProgress
import java.io.File

/**
 * File-backed or in-memory repository for persisting level progress records.
 */
class LocalProgressRepository(
    private val storageDir: File? = null
) : ProgressRepository {

    private val progressMap = mutableMapOf<String, LevelProgress>()
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val type = Types.newParameterizedType(List::class.java, LevelProgress::class.java)
    private val adapter = moshi.adapter<List<LevelProgress>>(type).indent("  ")

    init {
        loadFromDisk()
    }

    private fun loadFromDisk() {
        if (storageDir == null) return
        try {
            val file = File(storageDir, "progress_records.json")
            if (file.exists()) {
                val json = file.readText()
                val list = adapter.fromJson(json) ?: emptyList()
                list.forEach { progressMap[it.levelId] = it }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveToDisk() {
        if (storageDir == null) return
        try {
            if (!storageDir.exists()) storageDir.mkdirs()
            val file = File(storageDir, "progress_records.json")
            val list = progressMap.values.toList()
            val json = adapter.toJson(list)
            file.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getProgress(levelId: String): LevelProgress? {
        return progressMap[levelId]
    }

    override fun getAllProgress(): List<LevelProgress> {
        return progressMap.values.toList()
    }

    override fun saveProgress(progress: LevelProgress) {
        val existing = progressMap[progress.levelId]
        if (existing == null) {
            progressMap[progress.levelId] = progress
        } else {
            // Keep best result
            val bestScore = maxOf(existing.bestScore, progress.bestScore)
            val bestTime = if (existing.bestTimeSeconds == 0L) progress.bestTimeSeconds
                           else if (progress.bestTimeSeconds == 0L) existing.bestTimeSeconds
                           else minOf(existing.bestTimeSeconds, progress.bestTimeSeconds)

            val updated = progress.copy(
                completed = existing.completed || progress.completed,
                bestScore = bestScore,
                bestTimeSeconds = bestTime,
                attempts = existing.attempts + 1
            )
            progressMap[progress.levelId] = updated
        }
        saveToDisk()
    }

    override fun clearProgress() {
        progressMap.clear()
        saveToDisk()
    }
}

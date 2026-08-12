package ir.danialchoopan.lumalogic.data.repository

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import ir.danialchoopan.lumalogic.data.model.Achievement
import ir.danialchoopan.lumalogic.data.model.LevelProgress
import ir.danialchoopan.lumalogic.data.model.PlayerStats
import java.io.File

/**
 * Local persistent repository for player achievements.
 */
interface AchievementRepository {
    fun getAchievements(): List<Achievement>
    fun checkAndUnlockAchievements(stats: PlayerStats, latestProgress: LevelProgress?): List<Achievement>
    fun unlockAchievement(id: String): Boolean
}

class LocalAchievementRepository(
    private val storageDir: File? = null
) : AchievementRepository {

    private val defaultAchievements = listOf(
        Achievement("FIRST_LIGHT", "First Light", "Complete your first puzzle level.", "Lightbulb", targetCount = 1),
        Achievement("TEN_LEVELS", "Novice Optician", "Complete 10 levels.", "EmojiEvents", targetCount = 10),
        Achievement("FIFTY_LEVELS", "Spectrum Scholar", "Complete 50 levels.", "EmojiEvents", targetCount = 50),
        Achievement("CENTURY", "Optic Virtuoso", "Complete 100 levels.", "EmojiEvents", targetCount = 100),
        Achievement("MASTER_256", "LumaLogic Legend", "Complete all 256 levels!", "AutoAwesome", targetCount = 256),
        Achievement("THREE_STAR", "Shining Perfection", "Earn 3 stars on any level.", "Star", targetCount = 1),
        Achievement("STAR_COLLECTOR", "Constellation", "Collect 100 stars across all levels.", "Stars", targetCount = 100),
        Achievement("PERFECT_CHAPTER", "Chapter Master", "Earn 3 stars on all 16 levels of any chapter.", "WorkspacePremium", targetCount = 16),
        Achievement("ENERGY_MASTER", "Power Efficiency", "Complete a level with energy limit enabled.", "Bolt", targetCount = 1),
        Achievement("LOGIC_MASTER", "Logic Architect", "Complete a logic gate level.", "AccountTree", targetCount = 1),
        Achievement("NO_HINT", "Pure Intellect", "Complete 10 levels without using hints.", "Psychology", targetCount = 10)
    )

    private val achievementsMap = mutableMapOf<String, Achievement>()
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val type = Types.newParameterizedType(List::class.java, Achievement::class.java)
    private val adapter = moshi.adapter<List<Achievement>>(type)

    init {
        defaultAchievements.forEach { achievementsMap[it.id] = it }
        loadFromDisk()
    }

    private fun loadFromDisk() {
        if (storageDir == null) return
        try {
            val file = File(storageDir, "achievements.json")
            if (file.exists()) {
                val json = file.readText()
                val list = adapter.fromJson(json) ?: emptyList()
                list.forEach { saved ->
                    val existing = achievementsMap[saved.id]
                    if (existing != null) {
                        achievementsMap[saved.id] = existing.copy(
                            isUnlocked = saved.isUnlocked,
                            unlockedAt = saved.unlockedAt,
                            progress = saved.progress,
                            currentCount = saved.currentCount
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveToDisk() {
        if (storageDir == null) return
        try {
            if (!storageDir.exists()) storageDir.mkdirs()
            val file = File(storageDir, "achievements.json")
            val json = adapter.toJson(achievementsMap.values.toList())
            file.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getAchievements(): List<Achievement> {
        return achievementsMap.values.toList()
    }

    override fun unlockAchievement(id: String): Boolean {
        val achievement = achievementsMap[id] ?: return false
        if (achievement.isUnlocked) return false
        val unlocked = achievement.copy(
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis(),
            progress = 1.0f,
            currentCount = achievement.targetCount
        )
        achievementsMap[id] = unlocked
        saveToDisk()
        return true
    }

    override fun checkAndUnlockAchievements(stats: PlayerStats, latestProgress: LevelProgress?): List<Achievement> {
        val newlyUnlocked = mutableListOf<Achievement>()

        fun check(id: String, count: Int, condition: Boolean) {
            val ach = achievementsMap[id] ?: return
            if (ach.isUnlocked) return
            val current = minOf(count, ach.targetCount)
            val prog = current.toFloat() / ach.targetCount.toFloat()
            val shouldUnlock = condition || current >= ach.targetCount
            val updated = ach.copy(currentCount = current, progress = prog, isUnlocked = shouldUnlock, unlockedAt = if (shouldUnlock) System.currentTimeMillis() else 0L)
            achievementsMap[id] = updated
            if (shouldUnlock) newlyUnlocked.add(updated)
        }

        check("FIRST_LIGHT", stats.totalLevelsCompleted, stats.totalLevelsCompleted >= 1)
        check("TEN_LEVELS", stats.totalLevelsCompleted, stats.totalLevelsCompleted >= 10)
        check("FIFTY_LEVELS", stats.totalLevelsCompleted, stats.totalLevelsCompleted >= 50)
        check("CENTURY", stats.totalLevelsCompleted, stats.totalLevelsCompleted >= 100)
        check("MASTER_256", stats.totalLevelsCompleted, stats.totalLevelsCompleted >= 256)
        check("STAR_COLLECTOR", stats.totalStars, stats.totalStars >= 100)

        latestProgress?.let { prog ->
            if (prog.stars == 3) {
                check("THREE_STAR", 1, true)
            }
        }

        if (newlyUnlocked.isNotEmpty()) {
            saveToDisk()
        }
        return newlyUnlocked
    }
}

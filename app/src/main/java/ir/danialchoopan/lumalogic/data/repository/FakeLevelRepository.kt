package ir.danialchoopan.lumalogic.data.repository

import ir.danialchoopan.lumalogic.data.level.LevelRegistry
import ir.danialchoopan.lumalogic.data.model.Level

/**
 * Repository providing 256 built-in puzzle levels and integrating with user levels.
 */
class FakeLevelRepository(
    private val userLevelRepository: UserLevelRepository = LocalUserLevelRepository()
) : LevelRepository {

    override fun getDemoLevel(): Level {
        return LevelRegistry.getLevelById("chapter_01_level_01") ?: LevelRegistry.getAllLevels().first()
    }

    override fun getBuiltInLevels(): List<Level> {
        return LevelRegistry.getAllLevels()
    }

    override fun getUserLevels(): List<Level> {
        return userLevelRepository.getUserLevels()
    }

    override fun getLevels(): List<Level> {
        return getBuiltInLevels() + getUserLevels()
    }

    override fun getLevelById(id: String): Level? {
        val userLevel = userLevelRepository.getUserLevelById(id)
        if (userLevel != null) return userLevel
        return LevelRegistry.getLevelById(id)
    }

    override fun saveUserLevel(level: Level) {
        userLevelRepository.saveUserLevel(level)
    }

    override fun deleteUserLevel(id: String): Boolean {
        if (isBuiltInLevel(id)) {
            return false // Cannot delete built-in levels!
        }
        return userLevelRepository.deleteUserLevel(id)
    }

    override fun isBuiltInLevel(id: String): Boolean {
        return LevelRegistry.getLevelById(id) != null
    }
}

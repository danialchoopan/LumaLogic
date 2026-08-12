package ir.danialchoopan.lumalogic.data.repository

import ir.danialchoopan.lumalogic.data.model.Level

/**
 * Repository interface for managing built-in and user-created levels in LumaLogic.
 */
interface LevelRepository {
    fun getDemoLevel(): Level
    fun getLevels(): List<Level>
    fun getBuiltInLevels(): List<Level>
    fun getUserLevels(): List<Level>
    fun getLevelById(id: String): Level?
    fun saveUserLevel(level: Level)
    fun deleteUserLevel(id: String): Boolean
    fun isBuiltInLevel(id: String): Boolean
}

package ir.danialchoopan.lumalogic.data.repository

import ir.danialchoopan.lumalogic.data.model.Level

/**
 * Interface for managing user-created custom levels.
 */
interface UserLevelRepository {
    fun getUserLevels(): List<Level>
    fun getUserLevelById(id: String): Level?
    fun saveUserLevel(level: Level)
    fun deleteUserLevel(id: String): Boolean
    fun clearAll()
}

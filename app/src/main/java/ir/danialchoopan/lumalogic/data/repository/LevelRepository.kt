package ir.danialchoopan.lumalogic.data.repository

import ir.danialchoopan.lumalogic.data.model.Level

/**
 * Repository interface for managing levels in LumaLogic.
 */
interface LevelRepository {
    fun getDemoLevel(): Level
    fun getLevels(): List<Level>
    fun getLevelById(id: String): Level?
}

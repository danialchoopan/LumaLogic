package ir.danialchoopan.lumalogic.data.repository

import ir.danialchoopan.lumalogic.data.model.Level
import ir.danialchoopan.lumalogic.data.serialization.LevelJsonSerializer
import java.io.File

/**
 * File-backed repository storing user-created custom levels locally.
 */
class LocalUserLevelRepository(
    private val storageDir: File? = null,
    private val serializer: LevelJsonSerializer = LevelJsonSerializer()
) : UserLevelRepository {

    private val userLevelsMap = mutableMapOf<String, Level>()

    init {
        loadFromDisk()
    }

    private fun loadFromDisk() {
        if (storageDir == null) return
        try {
            val userLevelsFolder = File(storageDir, "user_levels")
            if (userLevelsFolder.exists()) {
                val files = userLevelsFolder.listFiles { file -> file.extension == "json" }
                files?.forEach { file ->
                    try {
                        val level = serializer.deserialize(file.readText())
                        userLevelsMap[level.levelId] = level.copy(isUserCreated = true)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveToDisk(level: Level) {
        if (storageDir == null) return
        try {
            val userLevelsFolder = File(storageDir, "user_levels")
            if (!userLevelsFolder.exists()) userLevelsFolder.mkdirs()
            val file = File(userLevelsFolder, "${level.levelId}.json")
            val json = serializer.serialize(level.copy(isUserCreated = true))
            file.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeFromDisk(levelId: String) {
        if (storageDir == null) return
        try {
            val userLevelsFolder = File(storageDir, "user_levels")
            val file = File(userLevelsFolder, "${levelId}.json")
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getUserLevels(): List<Level> {
        return userLevelsMap.values.toList()
    }

    override fun getUserLevelById(id: String): Level? {
        return userLevelsMap[id]
    }

    override fun saveUserLevel(level: Level) {
        val userLevel = level.copy(isUserCreated = true)
        userLevelsMap[userLevel.levelId] = userLevel
        saveToDisk(userLevel)
    }

    override fun deleteUserLevel(id: String): Boolean {
        if (userLevelsMap.containsKey(id)) {
            userLevelsMap.remove(id)
            removeFromDisk(id)
            return true
        }
        return false
    }

    override fun clearAll() {
        userLevelsMap.keys.toList().forEach { removeFromDisk(it) }
        userLevelsMap.clear()
    }
}

package ir.danialchoopan.lumalogic.data.repository

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

/**
 * Local persistent storage for player's favorite level IDs.
 */
interface FavoriteLevelRepository {
    fun isFavorite(levelId: String): Boolean
    fun toggleFavorite(levelId: String): Boolean
    fun getFavoriteLevelIds(): Set<String>
}

class LocalFavoriteLevelRepository(
    private val storageDir: File? = null
) : FavoriteLevelRepository {

    private val favoriteIds = mutableSetOf<String>()
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val type = Types.newParameterizedType(Set::class.java, String::class.java)
    private val adapter = moshi.adapter<Set<String>>(type)

    init {
        loadFromDisk()
    }

    private fun loadFromDisk() {
        if (storageDir == null) return
        try {
            val file = File(storageDir, "favorite_levels.json")
            if (file.exists()) {
                val json = file.readText()
                val set = adapter.fromJson(json) ?: emptySet()
                favoriteIds.addAll(set)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveToDisk() {
        if (storageDir == null) return
        try {
            if (!storageDir.exists()) storageDir.mkdirs()
            val file = File(storageDir, "favorite_levels.json")
            val json = adapter.toJson(favoriteIds)
            file.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun isFavorite(levelId: String): Boolean {
        return favoriteIds.contains(levelId)
    }

    override fun toggleFavorite(levelId: String): Boolean {
        val newState = if (favoriteIds.contains(levelId)) {
            favoriteIds.remove(levelId)
            false
        } else {
            favoriteIds.add(levelId)
            true
        }
        saveToDisk()
        return newState
    }

    override fun getFavoriteLevelIds(): Set<String> {
        return favoriteIds.toSet()
    }
}

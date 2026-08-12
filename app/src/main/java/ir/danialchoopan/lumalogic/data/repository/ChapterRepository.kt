package ir.danialchoopan.lumalogic.data.repository

import ir.danialchoopan.lumalogic.data.level.LevelRegistry
import ir.danialchoopan.lumalogic.data.model.Chapter
import ir.danialchoopan.lumalogic.data.model.Level

/**
 * Repository interface for managing levels by chapters and calculating chapter unlock status.
 */
interface ChapterRepository {
    fun getChapters(): List<Chapter>
    fun getChapter(id: String): Chapter?
    fun getLevelsForChapter(chapterId: String): List<Level>
    fun isChapterUnlocked(chapterId: String, totalStarsEarned: Int): Boolean
}

class DefaultChapterRepository : ChapterRepository {
    override fun getChapters(): List<Chapter> = LevelRegistry.chapters

    override fun getChapter(id: String): Chapter? {
        return LevelRegistry.chapters.find { it.id == id }
    }

    override fun getLevelsForChapter(chapterId: String): List<Level> {
        return LevelRegistry.getLevelsForChapter(chapterId)
    }

    override fun isChapterUnlocked(chapterId: String, totalStarsEarned: Int): Boolean {
        val chapter = getChapter(chapterId) ?: return false
        // Chapter 1 is always unlocked
        if (chapter.number == 1) return true
        return totalStarsEarned >= chapter.requiredStarsToUnlock
    }
}

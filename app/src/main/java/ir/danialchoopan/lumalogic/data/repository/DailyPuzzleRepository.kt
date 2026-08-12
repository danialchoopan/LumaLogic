package ir.danialchoopan.lumalogic.data.repository

import ir.danialchoopan.lumalogic.data.level.LevelRegistry
import ir.danialchoopan.lumalogic.data.model.Level
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repository generating and providing deterministic daily puzzles.
 */
interface DailyPuzzleRepository {
    fun getTodayPuzzle(): Level
    fun getTodayDateString(): String
}

class DefaultDailyPuzzleRepository : DailyPuzzleRepository {

    override fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US)
        return sdf.format(Date())
    }

    override fun getTodayPuzzle(): Level {
        val epochDays = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
        val allLevels = LevelRegistry.getAllLevels()
        val index = (epochDays % allLevels.size).toInt()
        val baseLevel = allLevels[index]

        return baseLevel.copy(
            name = "Daily Challenge: ${baseLevel.name}",
            description = "Daily Puzzle for ${getTodayDateString()}"
        )
    }
}

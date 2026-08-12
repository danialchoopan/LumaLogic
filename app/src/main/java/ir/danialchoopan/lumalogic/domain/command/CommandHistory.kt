package ir.danialchoopan.lumalogic.domain.command

import java.util.ArrayDeque

/**
 * CommandHistory managing dual undoStack and redoStack.
 */
class CommandHistory {
    private val undoStack = ArrayDeque<GameCommand>()
    private val redoStack = ArrayDeque<GameCommand>()

    fun execute(command: GameCommand) {
        undoStack.push(command)
        redoStack.clear()
    }

    fun undo(): GameCommand? {
        if (undoStack.isEmpty()) return null
        val cmd = undoStack.pop()
        redoStack.push(cmd)
        return cmd
    }

    fun redo(): GameCommand? {
        if (redoStack.isEmpty()) return null
        val cmd = redoStack.pop()
        undoStack.push(cmd)
        return cmd
    }

    fun canUndo(): Boolean = undoStack.isNotEmpty()
    fun canRedo(): Boolean = redoStack.isNotEmpty()

    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }

    fun undoCount(): Int = undoStack.size
    fun redoCount(): Int = redoStack.size
}

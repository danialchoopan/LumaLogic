package ir.danialchoopan.lumalogic.domain.action

import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.data.model.Rotation

/**
 * Interface and action structures preparing the architecture for future Undo/Redo stack support.
 */
sealed interface GameAction {

    data class RotateAction(
        val position: Position,
        val cellId: String,
        val previousRotation: Rotation,
        val newRotation: Rotation
    ) : GameAction

    data class MoveAction(
        val from: Position,
        val to: Position,
        val cellId: String
    ) : GameAction
}

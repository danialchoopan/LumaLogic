package ir.danialchoopan.lumalogic.domain.engine

import ir.danialchoopan.lumalogic.data.model.Direction
import ir.danialchoopan.lumalogic.data.model.GateType
import ir.danialchoopan.lumalogic.data.model.Rotation
import ir.danialchoopan.lumalogic.domain.model.GateState

/**
 * Gate logic module managing inputs, outputs, and truth tables for AND, OR, and NOT logic gates.
 */
object GateLogic {

    fun createInitialState(gateType: GateType): GateState {
        return when (gateType) {
            GateType.NOT -> GateState(gateType = gateType, inputA = false, inputB = false, output = true)
            GateType.AND, GateType.OR -> GateState(gateType = gateType, inputA = false, inputB = false, output = false)
        }
    }

    /**
     * Updates gate inputs and calculates output upon arrival of a beam.
     */
    fun processInput(
        currentState: GateState,
        incomingDirection: Direction,
        rotation: Rotation
    ): GateState {
        val (newA, newB) = when (currentState.gateType) {
            GateType.NOT -> {
                Pair(true, currentState.inputB)
            }
            GateType.AND, GateType.OR -> {
                val isPortA = isInputPortA(incomingDirection, rotation)
                if (isPortA) {
                    Pair(true, currentState.inputB)
                } else {
                    Pair(currentState.inputA, true)
                }
            }
        }

        val newOutput = evaluate(currentState.gateType, newA, newB)
        return currentState.copy(inputA = newA, inputB = newB, output = newOutput)
    }

    fun evaluate(gateType: GateType, inputA: Boolean, inputB: Boolean): Boolean {
        return when (gateType) {
            GateType.AND -> inputA && inputB
            GateType.OR -> inputA || inputB
            GateType.NOT -> !inputA
        }
    }

    fun getOutputDirection(gateType: GateType, rotation: Rotation): Direction {
        return when (rotation) {
            Rotation.ZERO -> Direction.UP
            Rotation.NINETY -> Direction.RIGHT
            Rotation.ONE_EIGHTY -> Direction.DOWN
            Rotation.TWO_SEVENTY -> Direction.LEFT
        }
    }

    private fun isInputPortA(incomingDirection: Direction, rotation: Rotation): Boolean {
        val primaryDir = getOutputDirection(GateType.AND, rotation)
        return incomingDirection == primaryDir || incomingDirection == rotateLeft(primaryDir)
    }

    private fun rotateLeft(direction: Direction): Direction {
        return when (direction) {
            Direction.UP -> Direction.LEFT
            Direction.LEFT -> Direction.DOWN
            Direction.DOWN -> Direction.RIGHT
            Direction.RIGHT -> Direction.UP
        }
    }
}

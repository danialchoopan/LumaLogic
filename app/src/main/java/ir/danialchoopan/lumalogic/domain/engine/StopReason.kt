package ir.danialchoopan.lumalogic.domain.engine

/**
 * Enum representing reasons why light tracing simulation stopped.
 */
enum class StopReason {
    TARGET_REACHED,
    OUT_OF_BOUNDS,
    BLOCKED,
    LOOP_DETECTED,
    NO_SOURCE,
    FILTER_BLOCKED,
    MAX_STEPS_REACHED,
    OUT_OF_ENERGY
}

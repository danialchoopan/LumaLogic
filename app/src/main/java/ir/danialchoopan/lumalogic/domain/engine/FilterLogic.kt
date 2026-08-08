package ir.danialchoopan.lumalogic.domain.engine

import ir.danialchoopan.lumalogic.data.model.LightColor

/**
 * Filter logic module validating beam color against filter requirements.
 */
object FilterLogic {

    /**
     * Determines whether a beam with [beamColor] passes through a filter expecting [acceptedColor].
     */
    fun shouldPass(beamColor: LightColor, acceptedColor: LightColor?): Boolean {
        return acceptedColor == null || beamColor == acceptedColor
    }
}

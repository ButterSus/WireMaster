package com.buttersus.wiremaster

import com.buttersus.wiremaster.client.designer.CameraMovementType
import com.buttersus.wiremaster.client.designer.Designer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
@Suppress("unused")
object WireMaster {
    // General
    const val MOD_ID = "wire-master"
    const val MOD_NAME = "Wire Master"

    // On change callback
    fun rebuildDependentCaches() {
        // Recompute all caches
        Designer.invalidateAllCaches()
    }

    // Options
    @JvmField
    var MOVEMENT_TYPE: CameraMovementType = CameraMovementType.NORMAL

    @JvmField
    var EXPERIMENTAL_ORTHOGRAPHIC: Boolean = false

    @JvmField
    var TRANSPARENT_PLAYERS: Boolean = false

    @JvmField
    var COUNTER_STRAFING: Boolean = true

    @JvmField
    var MAX_SPEED: Double = 25.0

    @JvmField
    var ACCELERATION: Double = 40.0

    @JvmField
    var SLOWDOWN: Double = 0.01  // 99% of velocity will be lost in 1 second

    @JvmField
    var REACH_DISTANCE: Double = 512.0
}
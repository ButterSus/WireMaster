package com.buttersus.wiremaster

import com.buttersus.wiremaster.client.camera.CameraMovementType
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
@Suppress("unused")
object WireMaster {
    // General
    const val MOD_ID = "wire-master"
    const val MOD_NAME = "Wire Master"

    // Options
    var MOVEMENT_TYPE: CameraMovementType = CameraMovementType.NORMAL
    var EXPERIMENTAL_ORTHOGRAPHIC: Boolean = false
    var TRANSPARENT_PLAYERS: Boolean = false
    var COUNTER_STRAFING: Boolean = true
    var MAX_SPEED: Double = 25.0
    var ACCELERATION: Double = 40.0
    var SLOWDOWN: Double = 0.01  // 99% of velocity will be lost in 1 second
    var REACH_DISTANCE: Double = 512.0
}
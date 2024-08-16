package com.buttersus.wiremaster

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
@Suppress("unused")
object WireMaster {
    // General
    const val MOD_ID = "wire-master"
    const val MOD_NAME = "Wire Master"

    // Options
    var PLANE_LOCKED_MOVEMENT: Boolean = false
    var EXPERIMENTAL_ORTHOGRAPHIC: Boolean = false
    var TRANSPARENT_PLAYERS: Boolean = false
    var COUNTER_STRAFING: Boolean = false
    var MAX_SPEED: Double = 25.0
    var ACCELERATION: Double = 40.0
    var SLOWDOWN: Double = 0.01  // 99% of velocity will be lost in 1 second
}
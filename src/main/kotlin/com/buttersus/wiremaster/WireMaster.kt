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
    var EXPERIMENTAL_ORTHOGRAPHIC: Boolean = false
}
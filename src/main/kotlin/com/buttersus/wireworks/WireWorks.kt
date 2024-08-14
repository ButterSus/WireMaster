package com.buttersus.wireworks

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
@Suppress("unused")
object WireWorks {
    // General
    const val MOD_ID = "wireworks"
    const val MOD_NAME = "Wire Works"

    // Options
    var EXPERIMENTAL_ORTHOGRAPHIC: Boolean = false
}
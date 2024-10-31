package com.buttersus.wiremaster.client.designer

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
enum class DesignerState {
    NORMAL,
    FLY,
    NAVIGATE
}

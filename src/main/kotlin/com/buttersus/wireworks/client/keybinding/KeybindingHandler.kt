package com.buttersus.wireworks.client.keybinding

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient

@Environment(EnvType.CLIENT)
object KeybindingHandler {
    @Suppress("UNUSED_PARAMETER")
    fun onClientTick(client: MinecraftClient) {
        if (Keybindings.TOGGLE_WIRE_DESIGNER.wasPressed()) println("Toggle Wire Designer")
    }
}
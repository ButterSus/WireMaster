package com.buttersus.wireworks.client.keybinding

import net.minecraft.client.MinecraftClient

object KeybindingHandler {
    fun onClientTick(client: MinecraftClient) {
        if (Keybindings.TOGGLE_WIRE_DESIGNER.wasPressed()) println("Toggle Wire Designer")
    }
}
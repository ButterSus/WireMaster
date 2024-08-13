package com.buttersus.wireworks.client.keybinding

import com.buttersus.wireworks.client.camera.OrthographicViewMode
import com.buttersus.wireworks.extensions.toInt
import net.minecraft.client.MinecraftClient

object KeybindingHandler {
    fun onClientTick(client: MinecraftClient) {
        if (Keybindings.TOGGLE_WIRE_DESIGNER.wasPressed()) {
            OrthographicViewMode.toggle()
        }

        if (OrthographicViewMode.isActive) {
            println("sus")
            val forward = client.options.forwardKey.isPressed.toInt() - client.options.backKey.isPressed.toInt()
            val sideways = client.options.rightKey.isPressed.toInt() - client.options.leftKey.isPressed.toInt()
            OrthographicViewMode.movePlayer(forward, sideways)
        }
    }
}
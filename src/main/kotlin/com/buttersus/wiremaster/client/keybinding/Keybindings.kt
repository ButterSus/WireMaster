package com.buttersus.wiremaster.client.keybinding

import com.buttersus.wiremaster.WireMaster
import de.siphalor.amecs.api.AmecsKeyBinding
import de.siphalor.amecs.api.KeyModifiers
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.util.InputUtil

@Suppress("MemberVisibilityCanBePrivate")
object Keybindings {
    val TOGGLE_WIRE_DESIGNER by lazy {
        AmecsKeyBinding(
            "key.${WireMaster.MOD_ID}.toggle_wire_designer",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.code,
            "category.${WireMaster.MOD_ID}.keybindings",
            KeyModifiers(),
        )
    }

    fun init() {
        KeyBindingHelper.registerKeyBinding(TOGGLE_WIRE_DESIGNER)
        ClientTickEvents.END_CLIENT_TICK.register(KeybindingHandler::onClientTick)
    }
}
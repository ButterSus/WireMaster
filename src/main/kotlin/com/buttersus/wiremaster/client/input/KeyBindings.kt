package com.buttersus.wiremaster.client.input

import com.buttersus.wiremaster.WireMaster
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper

@Suppress("MemberVisibilityCanBePrivate")
@Environment(EnvType.CLIENT)
object KeyBindings {
    val TOGGLE_WIRE_DESIGNER = ToggleWireDesignerKeyBinding()
    val TOGGLE_CONFIG_MENU = ToggleConfigMenuKeyBinding()
    val TOGGLE_CURSOR_MODE = ToggleCursorModeKeyBinding()
    val SCROLL_DOWN = ScrollKeyBinding("key.${WireMaster.MOD_ID}.scroll_down", -1.0)
    val SCROLL_UP = ScrollKeyBinding("key.${WireMaster.MOD_ID}.scroll_up", 1.0)
    val SPRINT = SprintKeyBinding()
    val MOVEMENT_CONTROL = MovementControlKeyBinding()

    fun init() {
        KeyBindingHelper.registerKeyBinding(TOGGLE_WIRE_DESIGNER)
        KeyBindingHelper.registerKeyBinding(TOGGLE_CONFIG_MENU)
        KeyBindingHelper.registerKeyBinding(TOGGLE_CURSOR_MODE)
        KeyBindingHelper.registerKeyBinding(SCROLL_DOWN)
        KeyBindingHelper.registerKeyBinding(SCROLL_UP)
        KeyBindingHelper.registerKeyBinding(SPRINT)
        KeyBindingHelper.registerKeyBinding(MOVEMENT_CONTROL)
    }
}

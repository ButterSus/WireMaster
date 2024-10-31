package com.buttersus.wiremaster.client.keybinding

import com.buttersus.wiremaster.WireMaster
import com.buttersus.wiremaster.util.interfaces.Initializable
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper

@Environment(EnvType.CLIENT)
object KeyBindingController : Initializable {
    @JvmField
    val TOGGLE_WIRE_DESIGNER = ToggleWireDesignerKeyBinding()

    @JvmField
    val TOGGLE_CONFIG_MENU = ToggleConfigMenuKeyBinding()

    @JvmField
    val TOGGLE_CURSOR_MODE = ToggleCursorModeKeyBinding()

    @JvmField
    val SCROLL_DOWN = ScrollKeyBinding("key.${WireMaster.MOD_ID}.scroll_down", -1.0)

    @JvmField
    val SCROLL_UP = ScrollKeyBinding("key.${WireMaster.MOD_ID}.scroll_up", 1.0)

    @JvmField
    val SPRINT = SprintKeyBinding()

    @JvmField
    val CURSOR_MOVEMENT = CursorMovementKeyBinding()

    override fun init() {
        KeyBindingHelper.registerKeyBinding(TOGGLE_WIRE_DESIGNER)
        KeyBindingHelper.registerKeyBinding(TOGGLE_CONFIG_MENU)
        KeyBindingHelper.registerKeyBinding(TOGGLE_CURSOR_MODE)
        KeyBindingHelper.registerKeyBinding(SCROLL_DOWN)
        KeyBindingHelper.registerKeyBinding(SCROLL_UP)
        KeyBindingHelper.registerKeyBinding(SPRINT)
        KeyBindingHelper.registerKeyBinding(CURSOR_MOVEMENT)
    }
}

package com.buttersus.wiremaster.client.input

import com.buttersus.wiremaster.WireMaster
import de.siphalor.amecs.api.AmecsKeyBinding
import de.siphalor.amecs.api.KeyBindingUtils
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper

@Environment(EnvType.CLIENT)
object KeyBindings {
    val TOGGLE_WIRE_DESIGNER: AmecsKeyBinding = ToggleWireDesignerKeyBinding()
    val TOGGLE_CURSOR_MODE: AmecsKeyBinding = ToggleCursorModeKeyBinding()
    val SCROLL_DOWN: AmecsKeyBinding = ScrollKeyBinding(
        "key.${WireMaster.MOD_ID}.scroll_down",
        KeyBindingUtils.MOUSE_SCROLL_DOWN,
        "category.${WireMaster.MOD_ID}.keybindings"
    )
    val SCROLL_UP: AmecsKeyBinding = ScrollKeyBinding(
        "key.${WireMaster.MOD_ID}.scroll_up",
        KeyBindingUtils.MOUSE_SCROLL_UP,
        "category.${WireMaster.MOD_ID}.keybindings"
    )

    fun init() {
        KeyBindingHelper.registerKeyBinding(TOGGLE_WIRE_DESIGNER)
        KeyBindingHelper.registerKeyBinding(TOGGLE_CURSOR_MODE)
        KeyBindingHelper.registerKeyBinding(SCROLL_DOWN)
        KeyBindingHelper.registerKeyBinding(SCROLL_UP)
    }
}

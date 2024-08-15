package com.buttersus.wiremaster.client.keybinding

import com.buttersus.wiremaster.WireMaster
import com.buttersus.wiremaster.client.camera.WireDesigner
import de.siphalor.amecs.api.AmecsKeyBinding
import de.siphalor.amecs.api.KeyModifiers
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.util.InputUtil

@Environment(EnvType.CLIENT)
@Suppress("MemberVisibilityCanBePrivate")
object Keybindings {
    val TOGGLE_WIRE_DESIGNER by lazy {
        AmecsKeyBinding(
            "key.${WireMaster.MOD_ID}.toggle_wire_designer",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.code,
            "category.${WireMaster.MOD_ID}.keybindings",
            KeyModifiers()
        )
    }

    fun init() {
        KeyBindingHelper.registerKeyBinding(TOGGLE_WIRE_DESIGNER)
    }

    fun onHandleKeybindings() {
        if (TOGGLE_WIRE_DESIGNER.wasPressed()) WireDesigner.toggle()
    }
}

package com.buttersus.wiremaster.client.input

import com.buttersus.wiremaster.WireMaster
import com.buttersus.wiremaster.client.camera.WireDesigner
import de.siphalor.amecs.api.AmecsKeyBinding
import de.siphalor.amecs.api.KeyModifiers
import de.siphalor.amecs.api.PriorityKeyBinding
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.InputUtil

@Environment(EnvType.CLIENT)
internal class ToggleWireDesignerKeyBinding : AmecsKeyBinding(
    "key.${WireMaster.MOD_ID}.toggle_wire_designer",
    InputUtil.Type.KEYSYM,
    InputUtil.UNKNOWN_KEY.code,
    "category.${WireMaster.MOD_ID}.keybindings",
    KeyModifiers()
), PriorityKeyBinding {
    override fun onPressedPriority(): Boolean {
        if (!WireDesigner.canToggle()) return false
        return WireDesigner.toggleWireDesigner()
    }
}

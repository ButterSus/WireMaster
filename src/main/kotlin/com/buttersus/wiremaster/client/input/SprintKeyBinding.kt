package com.buttersus.wiremaster.client.input

import com.buttersus.wiremaster.WireMaster
import com.buttersus.wiremaster.client.camera.WireDesigner
import de.siphalor.amecs.api.AmecsKeyBinding
import de.siphalor.amecs.api.KeyModifiers
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.InputUtil

@Environment(EnvType.CLIENT)
class SprintKeyBinding : AmecsKeyBinding(
    "key.${WireMaster.MOD_ID}.sprint",
    InputUtil.Type.KEYSYM,
    InputUtil.UNKNOWN_KEY.code,  // By default: Left Shift
    "category.${WireMaster.MOD_ID}.keybindings",
    KeyModifiers()
) {
    override fun onPressed() {
        if (!WireDesigner.canHoldSprint()) return
        WireDesigner.onSprintPress()
    }

    override fun onReleased() {
        if (!WireDesigner.canHoldSprint()) return
        WireDesigner.onSprintRelease()
    }
}
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
class ScrollKeyBinding(
    id: String,
    private val scrollY: Double
) : AmecsKeyBinding(
    id,
    InputUtil.Type.MOUSE,
    InputUtil.UNKNOWN_KEY.code,  // By default: Mouse Wheel
    "category.${WireMaster.MOD_ID}.keybindings",
    KeyModifiers()
), PriorityKeyBinding {
    override fun onPressedPriority(): Boolean {
        if (!WireDesigner.canScroll()) return false
        return WireDesigner.onMouseScroll(scrollY)
    }
}

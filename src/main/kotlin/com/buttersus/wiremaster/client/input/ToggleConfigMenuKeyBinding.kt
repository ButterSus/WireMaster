package com.buttersus.wiremaster.client.input

import com.buttersus.wiremaster.WireMaster
import com.buttersus.wiremaster.config.WireMasterConfig
import de.siphalor.amecs.api.AmecsKeyBinding
import de.siphalor.amecs.api.KeyModifiers
import de.siphalor.amecs.api.PriorityKeyBinding
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.InputUtil

@Environment(EnvType.CLIENT)
class ToggleConfigMenuKeyBinding : AmecsKeyBinding(
    "key.${WireMaster.MOD_ID}.toggle_config_menu",
    InputUtil.Type.KEYSYM,
    InputUtil.GLFW_KEY_E,
    "category.${WireMaster.MOD_ID}.keybindings",
    KeyModifiers(false, true, false)
), PriorityKeyBinding {
    override fun onPressedPriority(): Boolean {
        if (!WireMasterConfig.canToggleConfigMenu()) return false
        return WireMasterConfig.toggleConfigMenu()
    }
}

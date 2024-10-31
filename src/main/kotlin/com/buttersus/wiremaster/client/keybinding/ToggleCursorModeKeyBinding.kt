package com.buttersus.wiremaster.client.keybinding

import com.buttersus.wiremaster.WireMaster
import com.buttersus.wiremaster.client.designer.Designer
import com.buttersus.wiremaster.client.designer.DesignerPermissions
import de.siphalor.amecs.api.AmecsKeyBinding
import de.siphalor.amecs.api.KeyModifiers
import de.siphalor.amecs.api.PriorityKeyBinding
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.InputUtil

@Environment(EnvType.CLIENT)
class ToggleCursorModeKeyBinding : AmecsKeyBinding(
    "key.${WireMaster.MOD_ID}.toggle_cursor_mode",
    InputUtil.Type.KEYSYM,
    InputUtil.UNKNOWN_KEY.code,  // By default: Q
    "category.${WireMaster.MOD_ID}.keybindings",
    KeyModifiers()
), PriorityKeyBinding {
    override fun onPressedPriority(): Boolean {
        if (!DesignerPermissions.canToggleCursor()) return false
        Designer.cursorController.toggle()
        return true
    }
}
package com.buttersus.wiremaster.client.input

import com.buttersus.wiremaster.client.camera.WireDesigner
import de.siphalor.amecs.api.AmecsKeyBinding
import de.siphalor.amecs.api.KeyBindingUtils
import de.siphalor.amecs.api.KeyModifiers
import de.siphalor.amecs.api.PriorityKeyBinding
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil

@Environment(EnvType.CLIENT)
internal class ScrollKeyBinding(
    id: String,
    private val code: Int,
    category: String
) : AmecsKeyBinding(
    id, InputUtil.Type.MOUSE, code, category, KeyModifiers()
), PriorityKeyBinding {
    private val mc = MinecraftClient.getInstance()

    private fun getMouseX(): Double = mc.mouse.x * mc.window.scaledWidth / mc.window.width
    private fun getMouseY(): Double = mc.mouse.y * mc.window.scaledHeight / mc.window.height

    override fun onPressedPriority(): Boolean {
        if (!WireDesigner.canScroll()) return false
        return WireDesigner.onMouseScroll(
            getMouseX(), getMouseY(), when (code) {
                KeyBindingUtils.MOUSE_SCROLL_UP -> 1.0
                KeyBindingUtils.MOUSE_SCROLL_DOWN -> -1.0
                else -> return false
            }
        )
    }
}

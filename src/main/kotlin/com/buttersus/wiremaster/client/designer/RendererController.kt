package com.buttersus.wiremaster.client.designer

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.Perspective
import org.lwjgl.glfw.GLFW

@Environment(EnvType.CLIENT)
class RendererController(
    private val mc: MinecraftClient
) {
    // Methods
    private var oldPerspective: Perspective? = null

    fun overridePerspective() {
        oldPerspective = mc.options.perspective
        mc.options.perspective = Perspective.THIRD_PERSON_BACK
        if (oldPerspective?.isFirstPerson != mc.options.perspective.isFirstPerson)
            mc.gameRenderer.onCameraEntitySet(if (mc.options.perspective.isFirstPerson) mc.getCameraEntity() else null)
    }

    fun restorePerspective() {
        if (oldPerspective == null) return
        val perspective = mc.options.perspective
        mc.options.perspective = oldPerspective
        if (perspective.isFirstPerson != mc.options.perspective.isFirstPerson)
            mc.gameRenderer.onCameraEntitySet(if (mc.options.perspective.isFirstPerson) mc.getCameraEntity() else null)
        oldPerspective = null
    }

    fun showCursor() {
        if (mc.window == null) throw IllegalStateException("Window is null")
        GLFW.glfwSetInputMode(mc.window.handle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL)
    }

    fun hideCursor() {
        if (mc.window == null) throw IllegalStateException("Window is null")
        GLFW.glfwSetInputMode(mc.window.handle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED)
    }

    // Overrides: Rendering
    fun onRenderCrosshairShouldOverrideIsFirstPerson(cameraType: Perspective): Boolean {
        return Designer.cameraController.isActive() || cameraType.isFirstPerson
    }

    fun onRenderItemInHandShouldOverrideIsFirstPerson(cameraType: Perspective): Boolean {
        return Designer.cameraController.isActive() || cameraType.isFirstPerson
    }

    fun shouldHideCrosshair(): Boolean {
        return Designer.cameraController.isActive()
    }

    fun shouldOverrideRenderBlockOutline(): Boolean {
        return Designer.cameraController.isActive()
    }
}
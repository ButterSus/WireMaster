package com.buttersus.wiremaster.client.designer

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient

/**
 * Use these methods to define if an action can be performed
 */
@Environment(EnvType.CLIENT)
object DesignerPermissions {
    private val mc = MinecraftClient.getInstance()

    // Helper methods
    private fun isOpenGUI() = mc.currentScreen != null
    private fun isPlayerInWorld() = mc.world != null && mc.player != null
    private fun isActive() = Designer.cameraController.isActive()
    private fun isCursorActive() = Designer.cursorController.isActive()

    // Permission methods
    fun canToggleWireDesigner(): Boolean {
        return !isOpenGUI() && isPlayerInWorld()
    }

    fun canToggleCursor(): Boolean {
        return isActive()
    }

    fun canHoldSprint(): Boolean {
        return isActive()
    }

    fun canHoldMovementControl(): Boolean {
        return isActive()
    }

    fun canScroll(): Boolean {
        return isActive() && isCursorActive()
    }
}

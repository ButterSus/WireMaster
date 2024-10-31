package com.buttersus.wiremaster.config

import net.minecraft.client.MinecraftClient

object WireMasterConfigPermissions {
    private val mc = MinecraftClient.getInstance()

    private fun isOpenGUI() = mc.currentScreen != null
    private fun isPlayerInWorld() = mc.world != null && mc.player != null

    fun canToggleConfigMenu() : Boolean {
        return !isOpenGUI() && isPlayerInWorld()
    }
}
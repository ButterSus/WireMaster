package com.buttersus.wireworks.client.camera

import com.buttersus.wireworks.mixin.CameraMixin
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.Camera
import net.minecraft.entity.MovementType
import net.minecraft.util.math.Vec3d

object OrthographicViewMode {
    var isActive = false
    private var originalCameraPos = Vec3d.ZERO
    private val cameraOffset = Vec3d(10.0, 10.0, 10.0)

    fun toggle() {
        println("toggle: $isActive")
        isActive = !isActive
        if (isActive) {
            activateOrthographicView()
        } else {
            deactivateOrthographicView()
        }
    }

    fun activateOrthographicView() {
        val camera = MinecraftClient.getInstance().gameRenderer.camera
        originalCameraPos = camera.pos
        updateCameraPosition(camera)
    }

    fun deactivateOrthographicView() {
        val camera = MinecraftClient.getInstance().gameRenderer.camera
        (camera as CameraMixin).setPos(originalCameraPos)
    }

    fun updateCameraPosition(camera: Camera) {
        if (!isActive) return
        val player = MinecraftClient.getInstance().player ?: return
        (camera as CameraMixin).setPos(player.pos.add(cameraOffset))
    }

    fun movePlayer(forward: Int, sideways: Int) {
        if (!isActive) return
        val player = MinecraftClient.getInstance().player ?: return
        val moveVec = Vec3d(sideways.toDouble(), 0.0, -forward.toDouble()).normalize()
        player.move(MovementType.SELF, moveVec.multiply(0.1))
    }
}

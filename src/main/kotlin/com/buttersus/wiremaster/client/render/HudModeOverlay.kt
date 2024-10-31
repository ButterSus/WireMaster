package com.buttersus.wiremaster.client.render

import com.buttersus.wiremaster.WireMaster
import com.buttersus.wiremaster.client.designer.DesignerState
import com.buttersus.wiremaster.client.designer.Designer
import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

@Environment(EnvType.CLIENT)
object HudModeOverlay : HudRenderCallback {
    private val mc = MinecraftClient.getInstance()

    override fun onHudRender(matrixStack: MatrixStack?, tickDelta: Float) {
        val textureIdentifier = Identifier(
            WireMaster.MOD_ID, when (Designer.designerStateController.state) {
                DesignerState.NORMAL -> "textures/gui/mode_normal.png"
                DesignerState.FLY -> "textures/gui/mode_fly.png"
                DesignerState.NAVIGATE -> "textures/gui/mode_cursor.png"
            }
        )

        val windowWidth = mc.window.scaledWidth
        val windowHeight = mc.window.scaledHeight

        val (textureWidth, textureHeight) = 115 / 2 to 26 / 2
        val (x, y) = windowWidth - textureWidth - 8 to windowHeight - textureHeight - 8
        RenderSystem.setShader(GameRenderer::getPositionTexProgram)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, textureIdentifier)
        DrawableHelper.drawTexture(matrixStack, x, y, 0.0f, 0.0f, textureWidth, textureHeight, textureWidth, textureHeight)
    }
}

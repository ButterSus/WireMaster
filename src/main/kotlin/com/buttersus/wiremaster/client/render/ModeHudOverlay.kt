package com.buttersus.wiremaster.client.render

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack

object ModeHudOverlay : HudRenderCallback {
    private val mc = MinecraftClient.getInstance()

    @Suppress("UNUSED_VARIABLE")
    override fun onHudRender(matrixStack: MatrixStack?, tickDelta: Float) {
        val textRenderer = mc.textRenderer

        // Text
        val lines = arrayOf(
            "Placement: ${Colors.GREEN}y0 Bedrock Remover v89.1",
            "Regions: ${Colors.GREEN}5 ${Colors.RST}— Regions modified: ${Colors.RED}no",
        )

        // Position
        val (width, height) = mc.window.scaledWidth to mc.window.scaledHeight
        val (x0, y0) = 8f to -8f + height
        val (paddingX, paddingY) = 2 to 1
        val lineSpacing = 2

        for (i in 1..lines.size) {
            val posY = y0 - textRenderer.fontHeight * i - lineSpacing * (i - 1)
            val line = lines[lines.size - i]

            // Background
            DrawableHelper.fill(
                matrixStack,
                x0.toInt() - paddingX,
                posY.toInt() + textRenderer.fontHeight + paddingY,
                x0.toInt() + textRenderer.getWidth(line) + paddingX,
                posY.toInt() - paddingY,
                0x80000000.toInt()
            )

            // Text
            textRenderer.drawWithShadow(
                matrixStack,
                line,
                x0,
                posY,
                0xFFFFFF
            )
        }
    }
}

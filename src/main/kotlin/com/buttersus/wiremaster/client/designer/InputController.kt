package com.buttersus.wiremaster.client.designer

import com.buttersus.wiremaster.client.event.GameRendererTickEvents
import com.buttersus.wiremaster.client.event.MouseEvents
import com.buttersus.wiremaster.util.interfaces.Initializable
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.Input
import net.minecraft.client.network.ClientPlayerEntity

/**
 * Helps to override default controls on WASD, Shift, ...
 */
@Environment(EnvType.CLIENT)
class InputController(
    private val mc: MinecraftClient
) : ClientTickEvents.StartTick, Initializable, MouseEvents.PlayerTurn,
    GameRendererTickEvents.StartRenderWithFrameTimeTick {
    // Impl: Initializable
    override fun init() {
        ClientTickEvents.START_CLIENT_TICK.register(this)
        GameRendererTickEvents.START_RENDER_WITH_FRAME_TIME_TICK.register(this)
        MouseEvents.PLAYER_TURN.register(this)
    }

    // Methods
    private var oldInput: Input? = null

    fun overrideInput() {
        val player = mc.player ?: throw IllegalStateException("Player is null")
        Designer.inputCalculator.passInput(player.input)
        oldInput = player.input
        player.input = Input()
    }

    fun restoreInput() {
        if (oldInput == null) return
        val player = mc.player ?: throw IllegalStateException("Player is null")
        player.input = oldInput
        oldInput = null

    }

    // Impl: Events
    override fun onStartTick(client: MinecraftClient?) {
        if (!Designer.cameraController.isActive()) return

        // Disable F5 key
        while (mc.options.togglePerspectiveKey.isPressed) continue
        mc.options.togglePerspectiveKey.isPressed = false
        oldInput?.tick(false, 0.0f)
    }

    override fun onStartRenderWithFrameTimeTick(tickDelta: Float, startTime: Long, tick: Boolean, frameTime: Double) {
        MouseEvents.MOUSE_MOVE.invoker().onMouseMove(yRotAccumulatorNDC, xRotAccumulatorNDC)
    }

    // Mouse movement (We need to accumulate movement)
    private var yRotAccumulatorNDC = 0.0
    private var xRotAccumulatorNDC = 0.0

    override fun onPlayerTurn(player: ClientPlayerEntity, yRot: Double, xRot: Double) {
        if (Designer.cursorController.isActive() && !Designer.cursorController.shouldDisableCursor) {
            yRotAccumulatorNDC += yRot / mc.window.width * 2
            xRotAccumulatorNDC += xRot / mc.window.height * 2
        }
    }
}
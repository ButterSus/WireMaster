package com.buttersus.wiremaster.client.designer

import com.buttersus.wiremaster.client.event.GameRendererTickEvents
import com.buttersus.wiremaster.util.EventPhase
import com.buttersus.wiremaster.util.interfaces.Initializable
import com.buttersus.wiremaster.util.passers.InputPasser
import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.Input
import org.joml.Vector3d

class InputCalculator(
    private val mc: MinecraftClient
) : Initializable, InputPasser, GameRendererTickEvents.StartRenderWithFrameTimeTick {
    // Impl: InputPasser
    private lateinit var input: Input

    override fun passInput(input: Input) {
        if (this::input.isInitialized) return
        this.input = input
    }

    // Impl: Initializable
    override fun init() {
        GameRendererTickEvents.START_RENDER_WITH_FRAME_TIME_TICK.apply {
            EventPhase.POST_CAPTURE.setOrder(this)
            register(EventPhase.POST_CAPTURE, this@InputCalculator)
        }
    }

    // Methods
    fun calculateInputUnitVector(): Vector3d {
        if (!isInputActive()) return Vector3d(0.0)
        return Vector3d(
            (input.pressingLeft.compareTo(false) - input.pressingRight.compareTo(false)).toDouble(),
            (input.jumping.compareTo(false) - input.sneaking.compareTo(false)).toDouble(),
            (input.pressingForward.compareTo(false) - input.pressingBack.compareTo(false)).toDouble()
        ).normalize()
    }

    fun isInputActive(): Boolean {
        return input.pressingForward || input.pressingBack || input.pressingLeft ||
                input.pressingRight || input.jumping || input.sneaking
    }

    // Counter strafing detection
    private val lastInputVector = Vector3d()

    fun isCounterStrafing(): Boolean {
        return lastInputVector.dot(calculateInputUnitVector()) == -1.0
    }

    // Impl: Events
    override fun onStartRenderWithFrameTimeTick(tickDelta: Float, startTime: Long, tick: Boolean, frameTime: Double) {
        lastInputVector.set(calculateInputUnitVector())
    }
}
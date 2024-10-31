package com.buttersus.wiremaster.client.designer

import com.buttersus.wiremaster.client.event.GameRendererTickEvents
import com.buttersus.wiremaster.util.interfaces.Initializable
import com.buttersus.wiremaster.util.interpolation.ExponentialInterpolation
import com.buttersus.wiremaster.util.interpolation.SpringInterpolation
import org.joml.Vector3d

class InterpolationManager(
    position: Vector3d
) : Initializable, GameRendererTickEvents.StartRenderWithFrameTimeTick {
    val mouseScrollInterpolation = SpringInterpolation.createBasic(position)
    val cursorMovementInterpolation = ExponentialInterpolation.createQuick(position)

    // Methods
    fun resetForCursor() {
        mouseScrollInterpolation.resetVector()
        cursorMovementInterpolation.resetVector()
    }

    // Impl: Initializable
    override fun init() {
        GameRendererTickEvents.START_RENDER_WITH_FRAME_TIME_TICK.register(this)
    }

    // Impl: Events
    override fun onStartRenderWithFrameTimeTick(tickDelta: Float, startTime: Long, tick: Boolean, frameTime: Double) {
        mouseScrollInterpolation.update(frameTime)
        cursorMovementInterpolation.update(frameTime)
    }
}
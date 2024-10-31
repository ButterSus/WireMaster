package com.buttersus.wiremaster.client.event

import com.buttersus.wiremaster.client.event.GameRendererTickEvents.EndRenderTick
import com.buttersus.wiremaster.client.event.GameRendererTickEvents.StartRenderTick
import com.buttersus.wiremaster.client.event.GameRendererTickEvents.StartRenderWithFrameTimeTick
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

@Environment(EnvType.CLIENT)
object GameRendererTickEvents {
    // Used for frameTime calculation
    private var lastNanoTime: Long = -1

    @JvmStatic
    fun isValidFrameTime(): Boolean {
        return lastNanoTime != -1L
    }

    @JvmStatic
    fun getFrameTime(): Double {
        val currentNanoTime = System.nanoTime()
        val frameTime = (currentNanoTime - lastNanoTime) / 1e9
        return frameTime
    }

    @JvmStatic
    fun resetFrameTime() {
        lastNanoTime = -1
    }

    @JvmStatic
    fun updateFrameTime() {
        lastNanoTime = System.nanoTime()
    }

    /**
     * Called at the start of each render tick.
     */
    @JvmField
    val START_RENDER_TICK: Event<StartRenderTick> =
        EventFactory.createWithPhases(StartRenderTick::class.java, { callbacks ->
            StartRenderTick { tickDelta, startTime, tick ->
                for (callback in callbacks) {
                    callback.onStartRenderTick(tickDelta, startTime, tick)
                }
            }
        }, Event.DEFAULT_PHASE)

    /**
     * Called at the end of each render tick that has a frame time.
     * Is used to calculate movement.
     */
    @JvmField
    val START_RENDER_WITH_FRAME_TIME_TICK: Event<StartRenderWithFrameTimeTick> =
        EventFactory.createWithPhases(StartRenderWithFrameTimeTick::class.java, { callbacks ->
            StartRenderWithFrameTimeTick { tickDelta, startTime, tick, frameTime ->
                for (callback in callbacks) {
                    callback.onStartRenderWithFrameTimeTick(tickDelta, startTime, tick, frameTime)
                }
            }
        }, Event.DEFAULT_PHASE)

    @JvmField
    val END_RENDER_TICK: Event<EndRenderTick> =
        EventFactory.createWithPhases(EndRenderTick::class.java, { callbacks ->
            EndRenderTick { tickDelta, startTime, tick ->
                for (callback in callbacks) {
                    callback.onEndRenderTick(tickDelta, startTime, tick)
                }
            }
        }, Event.DEFAULT_PHASE)

    fun interface StartRenderTick {
        fun onStartRenderTick(tickDelta: Float, startTime: Long, tick: Boolean)
    }

    fun interface StartRenderWithFrameTimeTick {
        fun onStartRenderWithFrameTimeTick(tickDelta: Float, startTime: Long, tick: Boolean, frameTime: Double)
    }

    fun interface EndRenderTick {
        fun onEndRenderTick(tickDelta: Float, startTime: Long, tick: Boolean)
    }
}

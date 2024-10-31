package com.buttersus.wiremaster.client.event

import com.buttersus.wiremaster.client.event.MouseEvents.MouseMove
import com.buttersus.wiremaster.client.event.MouseEvents.MouseScrollY
import com.buttersus.wiremaster.client.event.MouseEvents.PlayerTurn
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.network.ClientPlayerEntity

@Environment(EnvType.CLIENT)
object MouseEvents {
    @JvmField
    val PLAYER_TURN: Event<PlayerTurn> =
        EventFactory.createWithPhases(PlayerTurn::class.java, { callbacks ->
            PlayerTurn { player, yRot, xRot ->
                for (callback in callbacks) {
                    callback.onPlayerTurn(player, yRot, xRot)
                }
            }
        }, Event.DEFAULT_PHASE)

    fun interface PlayerTurn {
        fun onPlayerTurn(player: ClientPlayerEntity, yRot: Double, xRot: Double)
    }

    @JvmField
    val MOUSE_MOVE: Event<MouseMove> =
        EventFactory.createWithPhases(MouseMove::class.java, { callbacks ->
            MouseMove { x, y ->
                for (callback in callbacks) {
                    callback.onMouseMove(x, y)
                }
            }
        }, Event.DEFAULT_PHASE)

    fun interface MouseMove {
        fun onMouseMove(x: Double, y: Double)
    }

    @JvmField
    val MOUSE_SCROLL_Y: Event<MouseScrollY> =
        EventFactory.createWithPhases(MouseScrollY::class.java, { callbacks ->
            MouseScrollY { scrollY ->
                for (callback in callbacks) {
                    callback.onScrollY(scrollY)
                }
            }
        }, Event.DEFAULT_PHASE)

    fun interface MouseScrollY {
        fun onScrollY(scrollY: Double)
    }
}
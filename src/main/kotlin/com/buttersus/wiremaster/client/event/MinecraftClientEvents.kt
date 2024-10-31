package com.buttersus.wiremaster.client.event

import com.buttersus.wiremaster.client.event.MinecraftClientEvents.ScreenClose
import com.buttersus.wiremaster.client.event.MinecraftClientEvents.ScreenOpen
import com.buttersus.wiremaster.client.event.MinecraftClientEvents.WorldLoad
import com.buttersus.wiremaster.client.event.MinecraftClientEvents.WorldUnload
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.world.ClientWorld

@Environment(EnvType.CLIENT)
object MinecraftClientEvents {
    @JvmField
    val WORLD_LOAD: Event<WorldLoad> =
        EventFactory.createWithPhases(WorldLoad::class.java, { callbacks ->
            WorldLoad { world ->
                for (callback in callbacks) {
                    callback.onWorldLoad(world)
                }
            }
        }, Event.DEFAULT_PHASE)

    @JvmField
    val WORLD_UNLOAD: Event<WorldUnload> =
        EventFactory.createWithPhases(WorldUnload::class.java, { callbacks ->
            WorldUnload { screen ->
                for (callback in callbacks) {
                    callback.onWorldUnload(screen)
                }
            }
        }, Event.DEFAULT_PHASE)

    fun interface WorldLoad {
        fun onWorldLoad(world: ClientWorld)
    }

    fun interface WorldUnload {
        fun onWorldUnload(screen: Screen)
    }

    @JvmField
    val SCREEN_OPEN: Event<ScreenOpen> =
        EventFactory.createWithPhases(ScreenOpen::class.java, { callbacks ->
            ScreenOpen { screen, isInGame, level ->
                for (callback in callbacks) {
                    callback.onScreenOpen(screen, isInGame, level)
                }
            }
        }, Event.DEFAULT_PHASE)

    @JvmField
    val SCREEN_CLOSE: Event<ScreenClose> =
        EventFactory.createWithPhases(ScreenClose::class.java, { callbacks ->
            ScreenClose { screen, isInGame, level ->
                for (callback in callbacks) {
                    callback.onScreenClose(screen, isInGame, level)
                }
            }
        }, Event.DEFAULT_PHASE)

    fun interface ScreenOpen {
        fun onScreenOpen(screen: Screen, isInGame: Boolean, level: Int)
    }

    fun interface ScreenClose {
        fun onScreenClose(screen: Screen, isInGame: Boolean, level: Int)
    }
}
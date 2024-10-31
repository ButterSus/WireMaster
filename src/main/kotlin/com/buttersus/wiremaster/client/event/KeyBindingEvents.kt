package com.buttersus.wiremaster.client.event

import com.buttersus.wiremaster.client.event.KeyBindingEvents.CursorMovementKeyPress
import com.buttersus.wiremaster.client.event.KeyBindingEvents.CursorMovementKeyRelease
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

@Environment(EnvType.CLIENT)
object KeyBindingEvents {
    @JvmField
    val CURSOR_MOVEMENT_PRESS: Event<CursorMovementKeyPress> =
        EventFactory.createWithPhases(CursorMovementKeyPress::class.java, { callbacks ->
            CursorMovementKeyPress {
                for (callback in callbacks) {
                    callback.onCursorMovementKeyPress()
                }
            }
        }, Event.DEFAULT_PHASE)

    fun interface CursorMovementKeyPress {
        fun onCursorMovementKeyPress()
    }

    @JvmField
    val CURSOR_MOVEMENT_RELEASE: Event<CursorMovementKeyRelease> =
        EventFactory.createWithPhases(CursorMovementKeyRelease::class.java, { callbacks ->
            CursorMovementKeyRelease {
                for (callback in callbacks) {
                    callback.onCursorMovementKeyRelease()
                }
            }
        }, Event.DEFAULT_PHASE)

    fun interface CursorMovementKeyRelease {
        fun onCursorMovementKeyRelease()
    }
}
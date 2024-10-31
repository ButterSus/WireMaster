package com.buttersus.wiremaster.util

import com.buttersus.wiremaster.WireMaster
import net.fabricmc.fabric.api.event.Event
import net.minecraft.util.Identifier

object EventPhase {
    interface SetOrderInterface {
        fun setOrder(event: Event<*>)
    }

    @Suppress("ClassName")
    object POST_CAPTURE : Identifier(WireMaster.MOD_ID, "post_capture"), SetOrderInterface {
        override fun setOrder(event: Event<*>) {
            event.addPhaseOrdering(Event.DEFAULT_PHASE, this)
        }
    }
}
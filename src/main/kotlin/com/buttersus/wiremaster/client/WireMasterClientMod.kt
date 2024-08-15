package com.buttersus.wiremaster.client

import com.buttersus.wiremaster.client.camera.WireDesigner
import com.buttersus.wiremaster.client.keybinding.Keybindings
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

object WireMasterClientMod : ClientModInitializer {
    override fun onInitializeClient() {
        Keybindings.init()
        ClientTickEvents.START_CLIENT_TICK.register {
            WireDesigner.onClientTickStart()
        }
    }
}
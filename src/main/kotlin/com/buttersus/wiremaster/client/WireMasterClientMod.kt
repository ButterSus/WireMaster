package com.buttersus.wiremaster.client

import com.buttersus.wiremaster.client.keybinding.Keybindings
import net.fabricmc.api.ClientModInitializer

object WireMasterClientMod : ClientModInitializer {
    override fun onInitializeClient() {
        Keybindings.init()
    }
}
package com.buttersus.wireworks.client

import com.buttersus.wireworks.client.keybinding.Keybindings
import net.fabricmc.api.ClientModInitializer

object WireWorksClientMod : ClientModInitializer {
    override fun onInitializeClient() {
        Keybindings.init()
    }
}
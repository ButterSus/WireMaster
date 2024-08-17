package com.buttersus.wiremaster.client

import com.buttersus.wiremaster.client.keybinding.Keybindings
import com.buttersus.wiremaster.config.WireMasterConfig
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
object WireMasterClientMod : ClientModInitializer {
    override fun onInitializeClient() {
        WireMasterConfig.load()
        Keybindings.init()
    }
}
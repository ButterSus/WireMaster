package com.buttersus.wireworks.client

import com.buttersus.wireworks.client.keybinding.Keybindings
import com.buttersus.wireworks.config.ConfigManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
object WireWorksClientMod : ClientModInitializer {
    override fun onInitializeClient() {
        ConfigManager.loadConfig()
        Keybindings.init()
    }
}
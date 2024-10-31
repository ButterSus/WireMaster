package com.buttersus.wiremaster.client

import com.buttersus.wiremaster.client.designer.Designer
import com.buttersus.wiremaster.client.keybinding.KeyBindingController
import com.buttersus.wiremaster.client.render.HudModeOverlay
import com.buttersus.wiremaster.client.render.HudTestOverlay
import com.buttersus.wiremaster.config.WireMasterConfig
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback

@Environment(EnvType.CLIENT)
object WireMasterClientMod : ClientModInitializer {
    override fun onInitializeClient() {
        // Load config
        WireMasterConfig.load()

        // Initialize submodules
        KeyBindingController.init()
        Designer.init()

        // HUD callbacks
        HudRenderCallback.EVENT.register(HudModeOverlay)
        HudRenderCallback.EVENT.register(HudTestOverlay)
    }
}
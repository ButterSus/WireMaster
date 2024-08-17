package com.buttersus.wiremaster.client

import com.buttersus.wiremaster.client.camera.WireDesigner
import com.buttersus.wiremaster.client.input.KeyBindings
import com.buttersus.wiremaster.client.render.ModeHudOverlay
import com.buttersus.wiremaster.config.WireMasterConfig
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback

@Environment(EnvType.CLIENT)
object WireMasterClientMod : ClientModInitializer {
    override fun onInitializeClient() {
        WireMasterConfig.load()
        KeyBindings.init()
        WireDesigner.init()
        HudRenderCallback.EVENT.register(ModeHudOverlay)
    }
}
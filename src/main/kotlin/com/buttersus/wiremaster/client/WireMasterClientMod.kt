package com.buttersus.wiremaster.client

import com.buttersus.wiremaster.client.camera.WireDesigner
import com.buttersus.wiremaster.client.input.KeyBindings
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
object WireMasterClientMod : ClientModInitializer {
    override fun onInitializeClient() {
        KeyBindings.init()
        WireDesigner.init()
    }
}
package com.buttersus.wiremaster.integration

import com.buttersus.wiremaster.config.WireMasterConfig
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
class ModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> = ConfigScreenFactory(WireMasterConfig::createGui)
}

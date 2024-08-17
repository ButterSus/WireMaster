package com.buttersus.wiremaster.config

import com.buttersus.wiremaster.WireMaster
import dev.isxander.yacl.api.ConfigCategory
import dev.isxander.yacl.api.Option
import dev.isxander.yacl.api.YetAnotherConfigLib
import dev.isxander.yacl.gui.controllers.BooleanController
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import java.io.File
import java.util.*

@Environment(EnvType.CLIENT)
object WireMasterConfig {
    private val CONFIG_FILE = File(
        FabricLoader.getInstance().configDir.resolve("fabric").toFile(),
        "${WireMaster.MOD_ID}.properties"
    )

    // Keys
    private const val EXPERIMENTAL_ORTHOGRAPHIC_KEY = "experimental_orthographic"

    // Save & load
    fun save() {
        val properties = Properties()
        properties.setProperty(EXPERIMENTAL_ORTHOGRAPHIC_KEY, WireMaster.EXPERIMENTAL_ORTHOGRAPHIC.toString())

        CONFIG_FILE.outputStream().use { outputStream ->
            properties.store(outputStream, "WireMaster Configuration")
        }
    }

    fun load() {
        val properties = Properties()
        if (CONFIG_FILE.exists())
            CONFIG_FILE.inputStream().use(properties::load)

        WireMaster.EXPERIMENTAL_ORTHOGRAPHIC =
            properties.getProperty(EXPERIMENTAL_ORTHOGRAPHIC_KEY, "false").toBoolean()
    }

    // Mod menu options screen
    fun createGui(parent: Screen): Screen =
        YetAnotherConfigLib.createBuilder()
            .title(Text.translatable("config.${WireMaster.MOD_ID}.title"))
            .category(
                ConfigCategory.createBuilder()
                    .name(Text.translatable("config.${WireMaster.MOD_ID}.general"))
                    .tooltip(Text.translatable("config.${WireMaster.MOD_ID}.general.tooltip"))
                    .option(
                        Option.createBuilder(Boolean::class.java)
                            .name(Text.translatable("config.${WireMaster.MOD_ID}.experimental_orthographic"))
                            .tooltip(Text.translatable("config.${WireMaster.MOD_ID}.experimental_orthographic.tooltip"))
                            .binding(
                                false,
                                { WireMaster.EXPERIMENTAL_ORTHOGRAPHIC },
                                { WireMaster.EXPERIMENTAL_ORTHOGRAPHIC = it }
                            )
                            .controller(::BooleanController)
                            .build()
                    )
                    .build()
            )
            .save(::save)
            .build()
            .generateScreen(parent)
}

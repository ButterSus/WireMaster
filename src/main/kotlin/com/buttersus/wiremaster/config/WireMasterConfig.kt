package com.buttersus.wiremaster.config

import com.buttersus.wiremaster.WireMaster
import com.buttersus.wiremaster.client.camera.CameraMovementType
import dev.isxander.yacl.api.ConfigCategory
import dev.isxander.yacl.api.Option
import dev.isxander.yacl.api.YetAnotherConfigLib
import dev.isxander.yacl.gui.controllers.BooleanController
import dev.isxander.yacl.gui.controllers.cycling.EnumController
import dev.isxander.yacl.gui.controllers.slider.DoubleSliderController
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import java.io.File
import java.util.*

@Environment(EnvType.CLIENT)
object WireMasterConfig {
    private val mc = MinecraftClient.getInstance()
    private val CONFIG_FILE = File(
        FabricLoader.getInstance().configDir.resolve("fabric").toFile(),
        "${WireMaster.MOD_ID}.properties"
    )

    // Keys
    private const val MOVEMENT_TYPE_KEY = "movement_type"
    private const val EXPERIMENTAL_ORTHOGRAPHIC_KEY = "experimental_orthographic"
    private const val TRANSPARENT_PLAYERS_KEY = "transparent_players"
    private const val COUNTER_STRAFING_KEY = "counter_strafing"
    private const val MAX_SPEED_KEY = "max_speed"
    private const val ACCELERATION_KEY = "acceleration"
    private const val SLOWDOWN_KEY = "slowdown"
    private const val REACH_DISTANCE_KEY = "reach_distance"

    // Save & load
    private fun save() {
        val properties = Properties()
        properties.setProperty(MOVEMENT_TYPE_KEY, WireMaster.MOVEMENT_TYPE.name)
        properties.setProperty(EXPERIMENTAL_ORTHOGRAPHIC_KEY, WireMaster.EXPERIMENTAL_ORTHOGRAPHIC.toString())
        properties.setProperty(TRANSPARENT_PLAYERS_KEY, WireMaster.TRANSPARENT_PLAYERS.toString())
        properties.setProperty(COUNTER_STRAFING_KEY, WireMaster.COUNTER_STRAFING.toString())
        properties.setProperty(MAX_SPEED_KEY, WireMaster.MAX_SPEED.toString())
        properties.setProperty(ACCELERATION_KEY, WireMaster.ACCELERATION.toString())
        properties.setProperty(SLOWDOWN_KEY, WireMaster.SLOWDOWN.toString())
        properties.setProperty(REACH_DISTANCE_KEY, WireMaster.REACH_DISTANCE.toString())

        CONFIG_FILE.outputStream().use { outputStream ->
            properties.store(outputStream, "WireMaster Configuration")
        }
    }

    fun load() {
        val properties = Properties()
        if (CONFIG_FILE.exists())
            CONFIG_FILE.inputStream().use(properties::load)

        WireMaster.MOVEMENT_TYPE =
            properties.getProperty(MOVEMENT_TYPE_KEY, "NORMAL").let(CameraMovementType::valueOf)
        WireMaster.EXPERIMENTAL_ORTHOGRAPHIC =
            properties.getProperty(EXPERIMENTAL_ORTHOGRAPHIC_KEY, "false").let(String::toBoolean)
        WireMaster.TRANSPARENT_PLAYERS =
            properties.getProperty(TRANSPARENT_PLAYERS_KEY, "false").let(String::toBoolean)
        WireMaster.COUNTER_STRAFING =
            properties.getProperty(COUNTER_STRAFING_KEY, "true").let(String::toBoolean)
        WireMaster.MAX_SPEED =
            properties.getProperty(MAX_SPEED_KEY, "25.0").let(String::toDouble)
        WireMaster.ACCELERATION =
            properties.getProperty(ACCELERATION_KEY, "40.0").let(String::toDouble)
        WireMaster.SLOWDOWN =
            properties.getProperty(SLOWDOWN_KEY, "0.01").let(String::toDouble)
        WireMaster.REACH_DISTANCE =
            properties.getProperty(REACH_DISTANCE_KEY, "512.0").let(String::toDouble)
    }

    // Mod menu options screen
    fun createGui(parent: Screen?): Screen =
        YetAnotherConfigLib.createBuilder()
            .title(Text.translatable("config.${WireMaster.MOD_ID}.title"))
            .category(
                ConfigCategory.createBuilder()
                    .name(Text.translatable("config.${WireMaster.MOD_ID}.general"))
                    .tooltip(Text.translatable("config.${WireMaster.MOD_ID}.general.tooltip"))
                    .option(
                        Option.createBuilder(CameraMovementType::class.java)
                            .name(Text.translatable("config.${WireMaster.MOD_ID}.movement_type"))
                            .tooltip(Text.translatable("config.${WireMaster.MOD_ID}.movement_type.tooltip"))
                            .binding(
                                CameraMovementType.NORMAL,
                                { WireMaster.MOVEMENT_TYPE },
                                { WireMaster.MOVEMENT_TYPE = it }
                            )
                            .controller(::EnumController)
                            .build(),
                    )
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
                    .option(
                        Option.createBuilder(Boolean::class.java)
                            .name(Text.translatable("config.${WireMaster.MOD_ID}.transparent_players"))
                            .tooltip(Text.translatable("config.${WireMaster.MOD_ID}.transparent_players.tooltip"))
                            .binding(
                                false,
                                { WireMaster.TRANSPARENT_PLAYERS },
                                { WireMaster.TRANSPARENT_PLAYERS = it }
                            )
                            .controller(::BooleanController)
                            .build()
                    )
                    .option(
                        Option.createBuilder(Boolean::class.java)
                            .name(Text.translatable("config.${WireMaster.MOD_ID}.counter_strafing"))
                            .tooltip(Text.translatable("config.${WireMaster.MOD_ID}.counter_strafing.tooltip"))
                            .binding(
                                true,
                                { WireMaster.COUNTER_STRAFING },
                                { WireMaster.COUNTER_STRAFING = it }
                            )
                            .controller(::BooleanController)
                            .build()
                    )
                    .option(
                        Option.createBuilder(Double::class.java)
                            .name(Text.translatable("config.${WireMaster.MOD_ID}.max_speed"))
                            .tooltip(Text.translatable("config.${WireMaster.MOD_ID}.max_speed.tooltip"))
                            .binding(
                                25.0,
                                { WireMaster.MAX_SPEED },
                                { WireMaster.MAX_SPEED = it }
                            )
                            .controller { option -> DoubleSliderController(option, 1.0, 50.0, 1.0) }
                            .build()
                    )
                    .option(
                        Option.createBuilder(Double::class.java)
                            .name(Text.translatable("config.${WireMaster.MOD_ID}.acceleration"))
                            .tooltip(Text.translatable("config.${WireMaster.MOD_ID}.acceleration.tooltip"))
                            .binding(
                                40.0,
                                { WireMaster.ACCELERATION },
                                { WireMaster.ACCELERATION = it }
                            )
                            .controller { option -> DoubleSliderController(option, 1.0, 100.0, 1.0) }
                            .build()
                    )
                    .option(
                        Option.createBuilder(Double::class.java)
                            .name(Text.translatable("config.${WireMaster.MOD_ID}.slowdown"))
                            .tooltip(Text.translatable("config.${WireMaster.MOD_ID}.slowdown.tooltip"))
                            .binding(
                                0.01,
                                { WireMaster.SLOWDOWN },
                                { WireMaster.SLOWDOWN = it }
                            )
                            .controller { option -> DoubleSliderController(option, 0.00, 1.0, 0.01) }
                            .build()
                    )
                    .option(
                        Option.createBuilder(Double::class.java)
                            .name(Text.translatable("config.${WireMaster.MOD_ID}.reach_distance"))
                            .tooltip(Text.translatable("config.${WireMaster.MOD_ID}.reach_distance.tooltip"))
                            .binding(
                                512.0,
                                { WireMaster.REACH_DISTANCE },
                                { WireMaster.REACH_DISTANCE = it }
                            )
                            .controller { option -> DoubleSliderController(option, 0.0, 1024.0, 1.0) }
                            .build()
                    )
                    .build()
            )
            .save(::save)
            .build()
            .generateScreen(parent)

    // Config menu
    fun toggleConfigMenu(): Boolean {
        if (mc.currentScreen == null) {
            mc.setScreen(createGui(mc.currentScreen))
            return true
        }
        return false
    }

    // Other methods
    fun canToggleConfigMenu() =
        mc.currentScreen == null && mc.world != null && mc.player != null  // Not in a GUI
}

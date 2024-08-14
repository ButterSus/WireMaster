package com.buttersus.wireworks.screen

import com.buttersus.wireworks.WireWorks
import com.buttersus.wireworks.config.ConfigManager
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.ConfigCategory
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
class ModConfigScreen(
    parent: Screen?
) : Screen(Text.translatable("config.${WireWorks.MOD_ID}.title")) {
    private val configScreen: Screen

    init {
        val builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.translatable("config.${WireWorks.MOD_ID}.title"))

        val general: ConfigCategory = builder.getOrCreateCategory(Text.translatable("config.${WireWorks.MOD_ID}.general"))
        general.addEntry(
            ConfigEntryBuilder.create()
                .startBooleanToggle(Text.translatable("config.${WireWorks.MOD_ID}.experimental_orthographic"), WireWorks.EXPERIMENTAL_ORTHOGRAPHIC)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("config.${WireWorks.MOD_ID}.experimental_orthographic.tooltip"))
                .setSaveConsumer { WireWorks.EXPERIMENTAL_ORTHOGRAPHIC = it }
                .build()
        )

        builder.setSavingRunnable {
            ConfigManager.saveConfig()
        }

        configScreen = builder.build()
    }

    override fun init() {
        super.init()
        this.client?.setScreen(configScreen)
    }
}
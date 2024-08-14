package com.buttersus.wireworks.config

import com.buttersus.wireworks.WireWorks
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.util.*

@Environment(EnvType.CLIENT)
object ConfigManager {
    private val CONFIG_FILE = File(FabricLoader.getInstance().configDir.toFile(), "${WireWorks.MOD_ID}.properties")

    fun loadConfig() {
        if (!CONFIG_FILE.exists()) return
        val properties = Properties()
        CONFIG_FILE.inputStream().use { properties.load(it) }
        WireWorks.EXPERIMENTAL_ORTHOGRAPHIC = properties.getProperty("experimental_orthographic", "false").toBoolean()
    }

    fun saveConfig() {
        val properties = Properties()
        properties.setProperty("experimental_orthographic", WireWorks.EXPERIMENTAL_ORTHOGRAPHIC.toString())
        CONFIG_FILE.outputStream().use { properties.store(it, "WireWorks Config") }
    }
}
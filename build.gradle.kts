@file:Suppress("LocalVariableName")

plugins {
    kotlin("jvm")
    id("fabric-loom")
    id("maven-publish")
    java
}

group = property("maven_group")!!
version = property("mod_version")!!

repositories {
    maven("https://maven.siphalor.de/")  // Amecs API
    maven("https://maven.terraformersmc.com/")  // Mod Menu
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    val amecs_api_minecraft_version = property("minecraft_version").toString().split(".").take(2).joinToString(".")
    modApi("de.siphalor:amecsapi-$amecs_api_minecraft_version:${property("amecs_api_version")}")
    modApi("com.terraformersmc:modmenu:${property("mod_menu_version")}")
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(mutableMapOf("version" to project.version))
        }
    }

    jar {
        from("LICENSE")
    }
}

kotlin {
    jvmToolchain(17)
}

java {
    withSourcesJar()
}

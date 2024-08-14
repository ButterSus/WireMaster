rootProject.name = "wire-master"
pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        mavenCentral()
        gradlePluginPortal()
    }

    val loom_version: String by settings
    val fabric_kotlin_version: String by settings
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
        id("fabric-loom") version loom_version
        id("org.jetbrains.kotlin.jvm") version fabric_kotlin_version
            .split("+kotlin.")[1]
            .split("+")[0]
    }
}

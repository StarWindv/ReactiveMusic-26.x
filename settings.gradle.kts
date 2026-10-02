import org.gradle.api.plugins.ExtraPropertiesExtension

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.6"
}

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    create(rootProject) {
        versions("1.19.2", "1.20.1", "1.21.1", "1.21.11", "26.2")
        branch("fabric")
        branch("forge") { versions("1.19.2", "1.20.1", "26.2") }
        branch("neoforge") { versions("1.21.1", "26.2") }
        // NeoForge 1.21.11 blocked on yarn-mappings-patch-neoforge update
        // Re-add: branch("neoforge") { versions("1.21.1", "1.21.11", "26.2") }
        // 26.2+ ships unobfuscated, so the yarn patch layer is no longer needed there
    }
}

rootProject.name = "ReactiveMusic"

// Stonecutter only injects versions/<mc>/gradle.properties into the common project of
// each version, but loom reads fabric.loom.disableObfuscation on the loader projects too.
// Mirror that flag (and anything added here later) onto every project of the version.
gradle.beforeProject {
    if (name == "26.2") {
        extensions.getByType(ExtraPropertiesExtension::class.java)
            .set("fabric.loom.disableObfuscation", "true")
    }
}

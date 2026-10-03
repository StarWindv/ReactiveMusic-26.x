@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask

plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.gradleup.shadow")
}

val loader = prop("loom.platform")!!
val minecraft: String = stonecutter.current.version
// Minecraft 26.x ships unobfuscated: loom runs without mappings/remapping
val noRemap = stonecutter.eval(minecraft, ">=26")
val common: Project = requireNotNull(stonecutter.node.sibling("")?.project) {
    "No common project for $project"
}

version = "${mod.version}+$minecraft"
base {
    archivesName.set("${mod.id}-$loader")
}
architectury {
    platformSetupLoomIde()
    neoForge()
}

val commonBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

configurations {
    compileClasspath.get().extendsFrom(commonBundle)
    runtimeClasspath.get().extendsFrom(commonBundle)
    get("developmentNeoForge").extendsFrom(commonBundle)
}

repositories {
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    if (noRemap) {
        // Unobfuscated Minecraft: no mappings or remapped configurations exist
        "implementation"("net.fabricmc:fabric-loader:${common.mod.dep("fabric_loader")}")
    } else {
        "mappings"(loom.layered {
            mappings("net.fabricmc:yarn:$minecraft+build.${common.mod.dep("yarn_build")}:v2")
            mappings("dev.architectury:yarn-mappings-patch-neoforge:${common.mod.dep("neoforge_patch")}")
        })
    }
    "neoForge"("net.neoforged:neoforge:${common.mod.dep("neoforge_loader")}")

    commonBundle(project(common.path, if (noRemap) "runtimeElements" else "namedElements")) { isTransitive = false }
    shadowBundle(project(common.path, if (noRemap) "runtimeElements" else "transformProductionNeoForge")) { isTransitive = false }
}

loom {
    runConfigs.all {
        isIdeConfigGenerated = true
        runDir = "../../../run/$minecraft/$loader"
        vmArgs("-Dmixin.debug.export=true", "-Dreactivemusic.performanceLogging=true")
    }
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraft, ">=26"))
        JavaVersion.VERSION_25
    else if (stonecutter.eval(minecraft, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.withType<JavaCompile>().configureEach {
    if (stonecutter.eval(minecraft, ">=26")) options.release.set(25)
}

tasks.jar {
    archiveClassifier = "dev"
}

if (noRemap) {
    // No remap step: the shaded jar is the published artifact
    tasks.named("shadowJar", AbstractArchiveTask::class.java) {
        archiveClassifier.set("")
    }
} else {
    tasks.named("shadowJar", AbstractArchiveTask::class.java) {
        archiveClassifier.set("dev-shadow")
    }
    tasks.named("remapJar", RemapJarTask::class.java) {
        injectAccessWidener = true
        input = tasks.shadowJar.get().archiveFile
        archiveClassifier = null
        dependsOn(tasks.shadowJar)
    }
}

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    // Set inside the remap branch above: "dev-shadow" when the remapped jar is the
    // published artifact, empty when the shaded jar is used directly.
    exclude("fabric.mod.json", "architectury.common.json")
}

tasks.processResources {
    properties(listOf("META-INF/neoforge.mods.toml", "pack.mcmeta"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to common.mod.prop("mc_dep_forgelike"),
        "loader" to common.mod.prop("loader_dep_neoforge")
    )
}

tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}

tasks.register<Copy>("buildAndCollect") {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
    if (noRemap) {
        from(tasks.named("shadowJar", AbstractArchiveTask::class.java).get().archiveFile)
        from(tasks.named("sourcesJar", AbstractArchiveTask::class.java).get().archiveFile)
    } else {
        from(tasks.named("remapJar", RemapJarTask::class.java).get().archiveFile,
            tasks.named("remapSourcesJar", RemapSourcesJarTask::class.java).get().archiveFile)
    }
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
    dependsOn("build")
}

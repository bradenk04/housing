import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.shadow)
}

group = "io.github.bradenk04"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    // Main APIs:
    compileOnly(libs.spigot.api)

    // Command Frameworks:
    implementation(libs.lamp.common)
    implementation(libs.lamp.bukkit)
    implementation(libs.lamp.brigadier)

    // Configuration Dependencies:
    implementation(libs.ktoml.core)
    implementation(libs.ktoml.file)

    // Database Dependencies:
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)

    // Extra Libs:
    implementation(libs.adventure.nbt)
    implementation(libs.kotlinx.coroutines)
}

tasks {
    withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
    }

    withType<KotlinJvmCompile> {
        compilerOptions {
            javaParameters = true
        }
    }

    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("revxrsal", "io.github.bradenk04.shaded.lamp")
        relocate("net.kyori", "io.github.bradenk04.shaded.kyori")
        minimize()
    }

    build {
        dependsOn(shadowJar)
    }
}
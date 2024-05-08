@file:Suppress("UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    kotlin("jvm") version "1.9.24"
    id("org.sonarqube") version "4.0.0.2929"
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false

    java
    `java-library`
    jacoco
}

val pGroup = "me.gamercoder215.battlecards"
val pVersion = "1.1.1-SNAPSHOT"
val pAuthor = "GamerCoder"

sonarqube {
    properties {
        property("sonar.projectKey", "GamerCoder215_BattleCards")
        property("sonar.organization", "gamercoder215")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

allprojects {
    group = pGroup
    version = pVersion
    description = "BattleCards is a unique, action-packed Minecraft Plugin, featuring upgradable and collectible cards used in Battle."

    apply<JavaPlugin>()
    apply<JavaLibraryPlugin>()
    apply(plugin = "maven-publish")

    repositories {
        mavenCentral()
        mavenLocal()

        maven("https://jitpack.io")
        maven("https://plugins.gradle.org/m2/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://oss.sonatype.org/content/repositories/central")

        maven("https://repo.codemc.org/repository/nms/")
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.jeff-media.com/nexus/repository/jeff-media-public/")
        maven("https://libraries.minecraft.net/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    project.ext["plugin_version"] = pVersion.split("-")[0]
}

val jvmVersion = JavaVersion.VERSION_1_8

subprojects {
    apply<JacocoPlugin>()
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.sonarqube")
    apply(plugin = "com.github.johnrengelman.shadow")

    dependencies {
        compileOnly("org.jetbrains:annotations:24.1.0")

        val kotlin = compileOnly("org.jetbrains.kotlin:kotlin-stdlib:${getKotlinPluginVersion()}")
        project.ext["kotlin_version"] = kotlin!!.version
        compileOnly(kotlin("reflect"))

        testImplementation("org.mockito:mockito-core:5.11.0")
        testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
        testImplementation(kotlin("test"))
    }

    java {
        sourceCompatibility = jvmVersion
        targetCompatibility = jvmVersion
    }

    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = jvmVersion.toString()
        }

        compileTestKotlin {
            kotlinOptions.jvmTarget = jvmVersion.toString()
        }

        jacocoTestReport {
            dependsOn(test)

            reports {
                xml.required.set(false)
                csv.required.set(false)

                html.required.set(true)
                html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
            }
        }

        test {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
            finalizedBy(jacocoTestReport)
        }

        jar {
            dependsOn("shadowJar")
            archiveClassifier.set("dev")
        }

        withType<ShadowJar> {
            manifest {
                attributes(
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to project.version,
                    "Implementation-Vendor" to pAuthor
                )
            }
            relocate("revxrsal.commands", "me.gamercoder215.battlecards.shaded.lamp")
            relocate("org.bstats", "me.gamercoder215.battlecards.shaded.bstats")
            relocate("com.jeff_media.updatechecker", "me.gamercoder215.battlecards.shaded.updatechecker")

            archiveClassifier.set("")
            archiveFileName.set("${project.name}-${project.version}.jar")
        }
    }

    artifacts {
        add("default", tasks.getByName<ShadowJar>("shadowJar"))
    }
}

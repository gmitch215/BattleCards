@file:Suppress("UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.8.10"
    id("org.sonarqube") version "4.0.0.2929"
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false

    java
    `maven-publish`
    `java-library`
    jacoco
}

val pGroup = "me.gamercoder215.battlecards"
val pVersion = "1.0.0-SNAPSHOT"
val pAuthor = "GamerCoder"

sonarqube {
    properties {
        property("sonar.projectKey", "${pAuthor}_BattleCards")
        property("sonar.organization", "gamercoder215")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

allprojects {
    group = pGroup
    version = pVersion
    description = "BattleCards is a premium, action-packed Minecraft Plugin, featuring upgradable and collectible cards used in Battle."

    repositories {
        mavenCentral()
        mavenLocal()

        maven("https://jitpack.io")
        maven("https://plugins.gradle.org/m2/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://oss.sonatype.org/content/repositories/central")

        maven("https://repo.codemc.org/repository/nms/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.jeff-media.com/nexus/repository/jeff-media-public/")
        maven("https://libraries.minecraft.net/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
}

val jvmVersion = JavaVersion.VERSION_1_8

subprojects {
    apply<JavaPlugin>()
    apply<JavaLibraryPlugin>()
    apply<JacocoPlugin>()
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.sonarqube")
    apply(plugin = "com.github.johnrengelman.shadow")

    dependencies {
        compileOnly("org.jetbrains:annotations:24.0.1")
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.8.20")

        testImplementation("org.mockito:mockito-core:5.3.0")
        testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")

        testImplementation("org.spigotmc:spigot-api:1.8-R0.1-SNAPSHOT")
        testImplementation("net.md-5:bungeecord-chat:1.16-R0.4")
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

        jar.configure {
            enabled = false
            dependsOn("shadowJar")
        }

        withType<ShadowJar> {
            manifest {
                attributes(
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to project.version,
                    "Implementation-Vendor" to pAuthor
                )
            }
            exclude("META-INF", "META-INF/**")

            relocate("revxrsal.commands", "me.gamercoder215.shaded.lamp")
            relocate("org.bstats", "me.gamercoder215.shaded.bstats")
            relocate("com.jeff_media.updatechecker", "me.gamercoder215.shaded.updatechecker")

            archiveFileName.set("${project.name}-${project.version}.jar")
        }
    }
}
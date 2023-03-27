import io.github.patrick.gradle.remapper.RemapTask

plugins {
    id("io.github.patrick.remapper") version "1.4.0"
}

val mcVersion = "1.19.3"
val jvmVersion = JavaVersion.VERSION_17

dependencies {
    api(project(":battlecards-abstract"))

    compileOnly("org.spigotmc:spigot:$mcVersion-R0.1-SNAPSHOT:remapped-mojang")
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

    assemble {
        dependsOn("remap")
    }

    remap {
        dependsOn("shadowJar")

        version.set(mcVersion)
        action.set(RemapTask.Action.MOJANG_TO_SPIGOT)
        archiveName.set("${project.name}-${project.version}.jar")
    }
}
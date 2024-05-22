val versions = mapOf(
    "1_8_R1" to 8,
    "1_8_R2" to 8,
    "1_8_R3" to 8,
    "1_9_R1" to 8,
    "1_9_R2" to 8,
    "1_10_R1" to 8,
    "1_11_R1" to 8,
    "1_12_R1" to 8,
    "1_13_R1" to 8,
    "1_13_R2" to 8,
    "1_14_R1" to 8,
    "1_15_R1" to 8,
    "1_16_R1" to 8,
    "1_16_R2" to 8,
    "1_16_R3" to 8,
    "1_17_R1" to 16,
    "1_18_R1" to 17,
    "1_18_R2" to 17,
    "1_19_R1" to 17,
    "1_19_R2" to 17,
    "1_19_R3" to 17,
    "1_20_R1" to 17,
    "1_20_R2" to 17,
    "1_20_R3" to 17
)

dependencies {
    // Spigot
    compileOnly("org.spigotmc:spigot-api:1.8-R0.1-SNAPSHOT") {
        version {
            strictly("1.8-R0.1-SNAPSHOT")
        }
    }

    // Implementation Dependencies
    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("com.jeff_media:SpigotUpdateChecker:3.0.3")

    // Soft Dependencies
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    // API
    api(project(":battlecards-api"))
    api(project(":battlecards-adventure"))

    versions.forEach {
        if (JavaVersion.current().isCompatibleWith(JavaVersion.toVersion(it.value)))
            api(project(":battlecards-${it.key}"))
    }
}


tasks {
    compileKotlin {
        if (JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_17))
            versions.filterValues { it >= 17 }.keys.forEach { dependsOn(project(":battlecards-$it").tasks["remap"]) }
    }

    kotlinSourcesJar {
        archiveClassifier.set("sources")
    }

    processResources {
        expand(project.properties)
    }

    shadowJar {
        dependsOn(kotlinSourcesJar)
    }
}
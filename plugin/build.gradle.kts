dependencies {
    // Spigot
    compileOnly("org.spigotmc:spigot-api:1.8-R0.1-SNAPSHOT") {
        version {
            strictly("1.8-R0.1-SNAPSHOT")
        }
    }

    // Implementation Dependencies
    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("com.jeff_media:SpigotUpdateChecker:3.0.2")

    // Soft Dependencies
    compileOnly("me.clip:placeholderapi:2.11.3")

    // API
    api(project(":battlecards-api"))

    listOf(
        "1_8_R1",
        "1_8_R2",
        "1_8_R3",
        "1_9_R1",
        "1_9_R2",
        "1_10_R1",
        "1_11_R1",
        "1_12_R1",
        "1_13_R1",
        "1_13_R2",
        "1_14_R1",
        "1_15_R1",
        "1_16_R1",
        "1_16_R2",
        "1_16_R3",
        "1_17_R1",
        "1_18_R1",
        "1_18_R2",
        "1_19_R1",
        "1_19_R2",
        "1_19_R3"
    ).forEach { api(project(":battlecards-$it")) }
}


tasks {
    kotlinSourcesJar {
        archiveClassifier.set("sources")
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }

    shadowJar {
        dependsOn("kotlinSourcesJar")
    }
}

artifacts {
    add("archives", tasks.kotlinSourcesJar)
}
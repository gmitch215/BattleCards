plugins {
    id("org.jetbrains.dokka") version "1.8.10"
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
}

description = "API for the Premium Plugin BattleCards"

tasks {
    kotlinSourcesJar {
        archiveClassifier.set("sources")
    }

    register("javadocJar", Jar::class.java) {
        dependsOn(dokkaJavadoc)

        archiveClassifier.set("javadoc")
        from(dokkaJavadoc.flatMap { it.outputDirectory })
    }

    shadowJar {
        dependsOn(kotlinSourcesJar, "javadocJar")
    }
}

artifacts {
    add("archives", tasks["javadocJar"])
    add("archives", tasks.kotlinSourcesJar)
}
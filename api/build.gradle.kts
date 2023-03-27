import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("org.jetbrains.dokka") version "1.8.10"
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8-R0.1-SNAPSHOT")
}

description = "API for the Premium Plugin BattleCards"

tasks {

    kotlinSourcesJar {
        archiveFileName.set("BattleCards-API-${project.version}-sources.jar")
    }

    register("javadocJar", Jar::class.java) {
        dependsOn(dokkaJavadoc)

        archiveFileName.set("BattleCards-API-${project.version}-javadoc.jar")
        from(dokkaJavadoc.flatMap { it.outputDirectory })
    }

    withType<ShadowJar> {
        dependsOn(kotlinSourcesJar, "javadocJar")
        archiveFileName.set("BattleCards-API-${project.version}.jar")
    }
}

artifacts {
    add("archives", tasks["javadocJar"])
}
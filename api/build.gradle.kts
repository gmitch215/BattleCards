plugins {
    id("org.jetbrains.dokka") version "1.8.10"
    `maven-publish`
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8-R0.1-SNAPSHOT")
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

publishing {
    val github = "GamerCoder215/BattleCards"

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                url.set("https://github.com/$github")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("${pom.url}/blob/master/LICENSE")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/$github.git")
                    developerConnection.set("scm:git:ssh://github.com/$github.git")
                    url.set(pom.url)
                }
            }

            artifact(tasks.shadowJar)
            artifact(tasks["javadocJar"])
            artifact(tasks.kotlinSourcesJar)
        }
    }
}
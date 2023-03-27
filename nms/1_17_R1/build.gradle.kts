val mcVersion = "1.17.1"
val jvmVersion = JavaVersion.VERSION_16

dependencies {
    api(project(":battlecards-abstract"))

    compileOnly("org.spigotmc:spigot:$mcVersion-R0.1-SNAPSHOT")
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
}
val lampVersion = "3.1.5"
val mcVersion = "1.13.2"

dependencies {
    api(project(":battlecards-abstract"))

    implementation("com.github.Revxrsal.Lamp:bukkit:$lampVersion")
    implementation("com.github.Revxrsal.Lamp:common:$lampVersion")

    compileOnly("org.spigotmc:spigot:$mcVersion-R0.1-SNAPSHOT")
}

tasks {
    compileKotlin {
        kotlinOptions.javaParameters = true
    }
}
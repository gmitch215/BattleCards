val mcVersion = "1.16.2"

dependencies {
    api(project(":battlecards-abstract"))

    compileOnly("org.spigotmc:spigot:$mcVersion-R0.1-SNAPSHOT")
    testImplementation("org.spigotmc:spigot:$mcVersion-R0.1-SNAPSHOT")
}
val mcVersion = "1.13.2"

dependencies {
    api(project(":battlecards-abstract"))

    compileOnly("org.spigotmc:spigot:$mcVersion-R0.1-SNAPSHOT")
}
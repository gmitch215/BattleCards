val mcVersion = "1.8.3"

dependencies {
    api(project(":battlecards-abstract"))

    compileOnly("org.spigotmc:spigot:$mcVersion-R0.1-SNAPSHOT")
}
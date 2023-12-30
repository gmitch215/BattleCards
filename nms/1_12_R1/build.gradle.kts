val mcVersion = "1.12.2"

dependencies {
    api(project(":battlecards-abstract"))

    compileOnly("org.spigotmc:spigot:$mcVersion-R0.1-SNAPSHOT")

    implementation("me.gamercoder215.superadvancements:superadvancements-spigot:1.1.1")
}
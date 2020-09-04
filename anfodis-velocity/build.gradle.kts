repositories {
    maven("https://repo.velocitypowered.com/releases") {
        content {
            includeGroup("com.velocitypowered")
        }
    }
}

dependencies {
    api(project(":anfodis-command-completion"))
    api(project(":anfodis-listener"))
    implementation("net.mcparkour:craftmon-velocity:1.0.8")
    compileOnly("com.velocitypowered:velocity-api:1.0.9")
}

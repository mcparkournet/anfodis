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
    compileOnly("com.velocitypowered:velocity-api:1.0.9")
}

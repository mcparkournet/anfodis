repositories {
    maven("https://papermc.io/repo/repository/maven-public") {
        content {
            includeGroup("com.destroystokyo.paper")
            includeGroup("net.md-5")
        }
    }
}

dependencies {
    api(project(":anfodis-command-completion"))
    api(project(":anfodis-listener"))
    implementation("net.mcparkour:craftmon-paper:1.0.8")
    compileOnly("com.destroystokyo.paper:paper-api:1.16.2-R0.1-SNAPSHOT")
}

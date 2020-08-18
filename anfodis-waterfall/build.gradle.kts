repositories {
    maven("https://papermc.io/repo/repository/maven-public") {
        content {
            includeGroup("io.github.waterfallmc")
            includeGroup("net.md-5")
        }
    }
}

dependencies {
    api(project(":anfodis-command-completion"))
    api(project(":anfodis-listener"))
    compileOnly("io.github.waterfallmc:waterfall-api:1.16-R0.4-SNAPSHOT")
}

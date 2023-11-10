repositories {
    maven("https://m2.dv8tion.net/releases") {
        content {
            includeGroup("net.dv8tion")
        }
    }
}

dependencies {
    api(project(":anfodis-command"))
    api(project(":anfodis-listener"))
    implementation("net.dv8tion:JDA:4.4.0_350")
}

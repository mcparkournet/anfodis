repositories {
	maven("https://repo.velocitypowered.com/releases") {
		content {
			includeGroup("com.velocitypowered")
		}
	}
}

dependencies {
	api(project(":anfodis-listener"))
	implementation("net.mcparkour:common-reflection:1.0.4")
	compileOnly("com.velocitypowered:velocity-api:1.0.5")
}

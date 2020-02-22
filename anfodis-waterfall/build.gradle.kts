repositories {
	maven("https://papermc.io/repo/repository/maven-public") {
		content {
			includeGroup("io.github.waterfallmc")
			includeGroup("net.md-5")
		}
	}
}

dependencies {
	api(project(":anfodis-listener"))
	implementation("net.mcparkour:common-reflection:1.0.3")
	compileOnly("io.github.waterfallmc:waterfall-api:1.15-SNAPSHOT")
}
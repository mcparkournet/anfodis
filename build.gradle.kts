import com.jfrog.bintray.gradle.BintrayExtension

plugins {
    `java-library`
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.5" apply false
}

subprojects {
    apply {
        plugin("java-library")
        plugin("maven-publish")
        plugin("com.jfrog.bintray")
    }

    repositories {
        jcenter()
    }

    dependencies {
        implementation("net.mcparkour:common-reflection:1.0.7")
        compileOnly("org.jetbrains:annotations:24.0.1")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
        testCompileOnly("org.jetbrains:annotations:24.0.1")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        withSourcesJar()
    }

    tasks {
        test {
            useJUnitPlatform()
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }

    configure<BintrayExtension> {
        user = properties["bintray-user"] as String?
        key = properties["bintray-api-key"] as String?
        publish = true
        setPublications("maven")
        pkg(closureOf<BintrayExtension.PackageConfig> {
            repo = properties["mcparkour-bintray-repository"] as String?
            userOrg = properties["mcparkour-bintray-organization"] as String?
            name = project.name
            desc = project.description
            websiteUrl = "https://github.com/mcparkournet/anfodis"
            issueTrackerUrl = "$websiteUrl/issues"
            vcsUrl = "$websiteUrl.git"
            setLicenses("MIT")
            setLabels("java", "annotation", "command", "listener")
        })
    }
}

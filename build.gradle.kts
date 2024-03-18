plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "net.azisaba"
version = "1.0.5"

repositories {
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/public/") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/public/") }
    maven { url = uri("https://repo.azisaba.net/repository/maven-public/") }
    maven { url = uri("https://repo.acrylicstyle.xyz/repository/maven-public/") }
}

dependencies {
    implementation("xyz.acrylicstyle.java-util:common:1.2.0-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("net.azisaba.azipluginmessaging:api:4.0.3")
}

tasks {
    processResources {
        // replace @version@ with project.version
        from(sourceSets.main.get().resources.srcDirs) {
            include("**/*.yml")
            expand(mapOf("version" to project.version))

            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        relocate("xyz.acrylicstyle.util", "net.azisaba.nitroplate.libs.xyz.acrylicstyle.util")
    }
}

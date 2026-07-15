plugins {
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.4.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "org.openredstone"
version = "1.2"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.15.2")
    implementation(kotlin("stdlib"))
}

tasks {
    shadowJar {
        // Avoid clobbering another plugin's relocated Kotlin classes on the classpath
        relocate("kotlin", "org.openredstone.libs.kotlin")
        archiveClassifier.set("")
    }
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21")
    }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

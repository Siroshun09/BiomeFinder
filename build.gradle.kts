plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("dev.siroshun.gradle.plugins.jcommon") version "1.6.0"
}

group = "com.github.siroshun09.biomefinder"
version = "1.11"
val mcVersion = "1.21.9"
val fullVersion = "${version}-mc${mcVersion}"

dependencies {
    paperweight.paperDevBundle("$mcVersion-R0.1-SNAPSHOT")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

jcommon {
    javaVersion = JavaVersion.VERSION_21
}

tasks {
    processResources {
        filesMatching(listOf("plugin.yml")) {
            expand("projectVersion" to fullVersion)
        }
    }

    jar {
        archiveFileName = "BiomeFinder-${fullVersion}.jar"
    }

    runServer {
        minecraftVersion(mcVersion)
        systemProperty("com.mojang.eula.agree", "true")
        systemProperty("paper.disablePluginRemapping", "true")
    }
}

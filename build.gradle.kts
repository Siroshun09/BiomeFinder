plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("dev.siroshun.gradle.plugins.jcommon") version "1.8.1"
}

group = "com.github.siroshun09.biomefinder"
version = "1.11"
val mcVersion = "26.1.1"
val fullVersion = "${version}-mc${mcVersion}"

dependencies {
    paperweight.paperDevBundle("$mcVersion.build.+")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

jcommon {
    javaVersion = JavaVersion.VERSION_25
}

tasks {
    processResources {
        filesMatching(listOf("plugin.yml")) {
            expand("projectVersion" to fullVersion, "minecraftVersion" to mcVersion)
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

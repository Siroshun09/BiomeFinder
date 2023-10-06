plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.7"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.github.siroshun09.biomefinder"
version = "1.7"
val mcVersion = "1.20.2"
val fullVersion = "${version}-mc${mcVersion}"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("$mcVersion-R0.1-SNAPSHOT")
    implementation("com.github.siroshun09.translationloader:translationloader:2.0.2")
}

tasks {
    reobfJar {
        outputJar.set(
            project.layout.buildDirectory
                .dir("libs")
                .get()
                .file("BiomeFinder-${fullVersion}.jar")
        )
    }

    build {
        dependsOn(reobfJar)
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    processResources {
        filesMatching(listOf("plugin.yml")) {
            expand("projectVersion" to fullVersion)
        }
    }

    shadowJar {
        minimize()
        relocate("com.github.siroshun09.translationloader", "com.github.siroshun09.biomefinder.libs.translationloader")
    }
}

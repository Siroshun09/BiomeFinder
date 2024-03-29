plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.11"
}

group = "com.github.siroshun09.biomefinder"
version = "1.9"
val mcVersion = "1.20.4"
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
}

tasks {
    build {
        dependsOn(reobfJar)
        doLast {
            val jarFile = project.layout.buildDirectory.dir("libs").get().file("BiomeFinder-${fullVersion}.jar").asFile
            jarFile.delete()
            reobfJar.flatMap { it.outputJar.asFile }.get().copyTo(jarFile)
        }
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
}

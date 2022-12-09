plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.11"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.github.siroshun09.biomefinder"
version = "1.5"
val mcVersion = "1.19.3"
val fullVersion = "${version}-mc${mcVersion}"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    paperDevBundle("$mcVersion-R0.1-SNAPSHOT")
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
        filteringCharset = Charsets.UTF_8.name()
    }

    shadowJar {
        minimize()
        relocate("com.github.siroshun09.translationloader", "com.github.siroshun09.biomefinder.libs.translationloader")
    }
}

bukkit {
    name = "BiomeFinder"
    main = "com.github.siroshun09.biomefinder.BiomeFinderPlugin"
    version = fullVersion
    apiVersion = "1.19"
    author = "Siroshun09"
    commands {
        register("findbiomes") {
            aliases = listOf("fb")
        }
        register("generateseed") {
            aliases = listOf("gs")
        }
    }
}

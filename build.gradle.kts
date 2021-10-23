plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.1.12"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
}

group = "com.github.siroshun09.biomefinder"
version = "1.0"
val mcVersion = "1.17.1"
val fullVersion = "${version}-mc${mcVersion}"

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

dependencies {
    paperDevBundle("$mcVersion-R0.1-SNAPSHOT")
}

tasks {
    reobfJar {
        outputJar.convention(
            project.layout.buildDirectory
                .file("libs/BiomeFinder-${fullVersion}.jar")
        )
    }

    build {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(16)
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}

bukkit {
    name = "BiomeFinder"
    main = "com.github.siroshun09.biomefinder.BiomeFinderPlugin"
    version = fullVersion
    apiVersion = "1.17"
    author = "Siroshun09"
    commands {
        register("findbiomes") {
            aliases = listOf("fb")
        }
    }
}

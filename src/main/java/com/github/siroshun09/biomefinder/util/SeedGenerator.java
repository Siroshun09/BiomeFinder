package com.github.siroshun09.biomefinder.util;

import com.github.siroshun09.biomefinder.wrapper.biome.MultiNoiseBiomeSourceWrapper;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class SeedGenerator {

    public static long generateSeedWithFixedSpawnBiome(@NotNull Key key) {
        return generateSeedWithFixedSpawnBiome(key, 0, 0, 0, false, 10000);
    }

    public static long generateSeedWithFixedSpawnBiome(@NotNull Key key, int x, int y, int z,
                                                       boolean large, int maxAttempts) {
        var random = new Random();

        int attempts = 0;

        while (attempts < maxAttempts) {
            long seed = random.nextLong();

            var biomeSource = large ? MultiNoiseBiomeSourceWrapper.largeBiomes(seed) : MultiNoiseBiomeSourceWrapper.overworld(seed);
            var spawnBiome = biomeSource.getBiome(x, y, z);

            if (key.equals(spawnBiome)) {
                return seed;
            } else {
                attempts++;
            }
        }

        return -1;
    }

    private SeedGenerator() {
        throw new UnsupportedOperationException();
    }
}

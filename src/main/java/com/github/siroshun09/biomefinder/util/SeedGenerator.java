package com.github.siroshun09.biomefinder.util;

import com.github.siroshun09.biomefinder.wrapper.biome.BiomeSource;
import com.github.siroshun09.biomefinder.wrapper.biome.MultiNoiseBiomeSourceWrapper;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;

import java.util.Random;
import java.util.function.Predicate;

@NullMarked
public final class SeedGenerator {

    public static long generateSeedWithFixedSpawnBiome(Key key) {
        return generateSeedByCondition(source -> {
            Key biomeKey = source.getBiome(0, 64, 0);
            return key.equals(biomeKey);
        }, false, 10000);
    }

    public static long generateSeedWithFixedSpawnBiome(Key key, int x, int y, int z,
                                                       boolean large, int maxAttempts) {
        return generateSeedByCondition(source -> {
            Key biomeKey = source.getBiome(x, y, z);
            return key.equals(biomeKey);
        }, large, maxAttempts);
    }

    public static long generateSeedByCondition(Predicate<BiomeSource> predicate, boolean large, int maxAttempts) {
        Random random = new Random();

        int attempts = 0;

        while (attempts < maxAttempts) {
            long seed = random.nextLong();

            BiomeSource biomeSource = large ? MultiNoiseBiomeSourceWrapper.largeBiomes(seed) : MultiNoiseBiomeSourceWrapper.overworld(seed);
            if (predicate.test(biomeSource)) {
                return seed;
            }
            attempts++;
        }

        return -1;
    }

    private SeedGenerator() {
        throw new UnsupportedOperationException();
    }
}

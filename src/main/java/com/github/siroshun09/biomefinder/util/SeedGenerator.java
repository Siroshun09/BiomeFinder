package com.github.siroshun09.biomefinder.util;

import net.kyori.adventure.key.Key;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.RandomState;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class SeedGenerator {

    public static long generateSeedWithFixedSpawnBiome(@NotNull Key key) {
        return generateSeedWithFixedSpawnBiome(key, 0, 0, 0, false, 10000);
    }

    public static long generateSeedWithFixedSpawnBiome(@NotNull Key key, int x, int y, int z,
                                                       boolean large, int maxAttempts) {
        var random = new Random();

        int biomeX = QuartPos.fromBlock(x);
        int biomeY = QuartPos.fromBlock(y);
        int biomeZ = QuartPos.fromBlock(z);

        var biomeResourceLocation = new ResourceLocation(key.namespace(), key.value());
        var biomeSource = NMSUtils.getBiomeSource(NMSUtils.Dimension.OVERWORLD);
        var settings = NMSUtils.getNoiseGeneratorSettings(NMSUtils.Dimension.OVERWORLD, large);

        int attempts = 0;

        while (attempts < maxAttempts) {
            long seed = random.nextLong();

            var state = RandomState.create(settings, NMSUtils.getNoiseParameters(), seed);
            var spawnBiome = biomeSource.getNoiseBiome(biomeX, biomeY, biomeZ, state.sampler());

            if (spawnBiome.is(biomeResourceLocation)) {
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

package com.github.siroshun09.biomefinder.util;

import net.minecraft.core.QuartPos;
import net.minecraft.world.level.levelgen.RandomState;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftNamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class SeedGenerator {

    public static long generateSeedWithFixedSpawnBiome(@NotNull Biome biome) {
        return generateSeedWithFixedSpawnBiome(biome, new Location(null, 0.0, 0.0, 0.0), false, 10000);
    }

    public static long generateSeedWithFixedSpawnBiome(@NotNull Biome biome, @NotNull Location spawnLocation,
                                                       boolean large, int maxAttempts) {
        var random = new Random();

        int biomeX = QuartPos.fromBlock(spawnLocation.getBlockX());
        int biomeY = QuartPos.fromBlock(spawnLocation.getBlockY());
        int biomeZ = QuartPos.fromBlock(spawnLocation.getBlockZ());

        var biomeResourceLocation = CraftNamespacedKey.toMinecraft(biome.getKey());
        var biomeSource = NMSUtils.getBiomeSource(NMSUtils.Dimension.OVERWORLD);
        var settings = NMSUtils.getNoiseGeneratorSettings(NMSUtils.Dimension.OVERWORLD, large);

        long seed;
        int attempts = 0;

        while (attempts < maxAttempts) {
            seed = random.nextLong();

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

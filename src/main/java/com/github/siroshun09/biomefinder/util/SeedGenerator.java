package com.github.siroshun09.biomefinder.util;

import com.github.siroshun09.biomefinder.BiomeFinderPlugin;
import net.kyori.adventure.key.Key;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.RandomState;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Random;

public final class SeedGenerator {

    @Deprecated(forRemoval = true, since = "1.8")
    @ApiStatus.ScheduledForRemoval(inVersion = "1.9")
    public static long generateSeedWithFixedSpawnBiome(@NotNull Biome biome) {
        logger().warn("SeedGenerator#generateSeedWithFixedSpawnBiome(Biome) has been deprecated. Use SeedGenerator#generateSeedWithFixedSpawnBiome(Key)");
        return generateSeedWithFixedSpawnBiome(biome.key(), 0, 0, 0, false, 10000);
    }

    @Deprecated(forRemoval = true, since = "1.8")
    @ApiStatus.ScheduledForRemoval(inVersion = "1.9")
    public static long generateSeedWithFixedSpawnBiome(@NotNull Biome biome, @NotNull Location spawnLocation,
                                                       boolean large, int maxAttempts) {
        logger().warn("SeedGenerator#generateSeedWithFixedSpawnBiome(Biome,Location,boolean,int) has been deprecated. Use SeedGenerator#generateSeedWithFixedSpawnBiome(Key,int,int,int,boolean,int)");
        return generateSeedWithFixedSpawnBiome(
                biome.key(),
                spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ(),
                large,
                maxAttempts
        );
    }

    private static Logger logger() {
        return JavaPlugin.getPlugin(BiomeFinderPlugin.class).getSLF4JLogger();
    }

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

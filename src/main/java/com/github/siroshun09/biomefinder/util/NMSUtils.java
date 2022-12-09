package com.github.siroshun09.biomefinder.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NMSUtils {

    public static @NotNull BiomeSource getBiomeSource(@NotNull Dimension dimension) {
        if (dimension == Dimension.OVERWORLD) {
            return MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(getRegistryAccess().lookupOrThrow(Registries.BIOME));
        } else {
            return MultiNoiseBiomeSource.Preset.NETHER.biomeSource(getRegistryAccess().lookupOrThrow(Registries.BIOME));
        }
    }

    public static @NotNull NoiseGeneratorSettings getNoiseGeneratorSettings(@NotNull Dimension dimension, boolean large) {
        if (dimension == Dimension.OVERWORLD) {
            return getRegistryAccess().lookupOrThrow(Registries.NOISE_SETTINGS).getOrThrow(large ? NoiseGeneratorSettings.LARGE_BIOMES : NoiseGeneratorSettings.OVERWORLD).value();
        } else {
            return getRegistryAccess().lookupOrThrow(Registries.NOISE_SETTINGS).getOrThrow(NoiseGeneratorSettings.NETHER).value();
        }
    }

    public static @NotNull HolderLookup<NormalNoise.NoiseParameters> getNoiseParameters() {
        return getRegistryAccess().lookupOrThrow(Registries.NOISE);
    }

    public static @Nullable String toBiomeKey(@NotNull Biome biome) {
        var key = getRegistryAccess().registryOrThrow(Registries.BIOME).getKey(biome);
        return key != null ? key.toString() : null;
    }

    public static @NotNull RegistryAccess.Frozen getRegistryAccess() {
        return MinecraftServer.getServer().registryAccess();
    }

    public enum Dimension {
        OVERWORLD, NETHER
    }
}

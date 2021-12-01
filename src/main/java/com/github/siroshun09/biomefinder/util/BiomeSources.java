package com.github.siroshun09.biomefinder.util;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.jetbrains.annotations.NotNull;

public final class BiomeSources {

    public static @NotNull NoiseBasedChunkGenerator getNoiseBasedChunkGenerator(long seed, boolean large) {
        return new NoiseBasedChunkGenerator(
                getNoiseParametersRegistry(),
                MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(getBiomeRegistry(), true),
                seed,
                () -> getNoiseGeneratorSettingsSupplier(large)
        );
    }

    public static @NotNull Registry<Biome> getBiomeRegistry() {
        return getRegistryHolder().registryOrThrow(Registry.BIOME_REGISTRY);
    }

    private static @NotNull Registry<NormalNoise.NoiseParameters> getNoiseParametersRegistry() {
        return getRegistryHolder().registryOrThrow(Registry.NOISE_REGISTRY);
    }

    private static @NotNull NoiseGeneratorSettings getNoiseGeneratorSettingsSupplier(boolean large) {
        return getRegistryHolder().registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY)
                .getOrThrow(large ? NoiseGeneratorSettings.LARGE_BIOMES : NoiseGeneratorSettings.OVERWORLD);
    }

    private static @NotNull RegistryAccess.RegistryHolder getRegistryHolder() {
        return ((CraftServer) Bukkit.getServer()).getServer().registryHolder;
    }
}

package com.github.siroshun09.biomefinder.util;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.jetbrains.annotations.NotNull;

public final class BiomeSources {

    public static @NotNull NoiseBasedChunkGenerator getNoiseBasedChunkGenerator(long seed, @NotNull Dimension dimension, boolean large) {
        var preset = dimension == Dimension.OVERWORLD ? MultiNoiseBiomeSource.Preset.OVERWORLD : MultiNoiseBiomeSource.Preset.NETHER;

        var settingsHolder = switch (dimension) {
            case OVERWORLD ->
                    getNoiseGeneratorSettingsRegistry().getOrCreateHolder(large ? NoiseGeneratorSettings.LARGE_BIOMES : NoiseGeneratorSettings.OVERWORLD);
            case NETHER -> getNoiseGeneratorSettingsRegistry().getOrCreateHolder(NoiseGeneratorSettings.NETHER);
        };

        return new NoiseBasedChunkGenerator(
                getStructureSet(),
                getNoiseParametersRegistry(),
                preset.biomeSource(getBiomeRegistry(), true),
                seed,
                settingsHolder
        );
    }

    public static @NotNull Registry<Biome> getBiomeRegistry() {
        return getRegistryHolder().registryOrThrow(Registry.BIOME_REGISTRY);
    }

    private static @NotNull Registry<StructureSet> getStructureSet() {
        return getRegistryHolder().registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
    }

    private static @NotNull Registry<NormalNoise.NoiseParameters> getNoiseParametersRegistry() {
        return getRegistryHolder().registryOrThrow(Registry.NOISE_REGISTRY);
    }

    private static Registry<NoiseGeneratorSettings> getNoiseGeneratorSettingsRegistry() {
        return getRegistryHolder().registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
    }

    private static RegistryAccess.Frozen getRegistryHolder() {
        return ((CraftServer) Bukkit.getServer()).getServer().registryHolder;
    }

    public enum Dimension {
        OVERWORLD, NETHER
    }
}

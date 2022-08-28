package com.github.siroshun09.biomefinder.util;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class NMSUtils {

    public static @NotNull BiomeSource getBiomeSource() {
        return MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(BuiltinRegistries.BIOME);
    }

    public static @NotNull NoiseGeneratorSettings getNoiseGeneratorSettings(boolean large) {
        return BuiltinRegistries.NOISE_GENERATOR_SETTINGS.getOrCreateHolderOrThrow(
                large ? NoiseGeneratorSettings.LARGE_BIOMES : NoiseGeneratorSettings.OVERWORLD
        ).value();
    }

    public static @NotNull Registry<NormalNoise.NoiseParameters> getNoiseParameters() {
        return BuiltinRegistries.NOISE;
    }

    public static @NotNull ChunkGenerator getChunkGenerator(long seed, @NotNull Dimension dimension, boolean large) {
        var preset = BuiltinRegistries.WORLD_PRESET.get(large ? WorldPresets.LARGE_BIOMES : WorldPresets.NORMAL);

        if (preset == null) {
            throw new IllegalStateException("preset not found");
        }

        var setting = preset.createWorldGenSettings(seed, false, false);

        return switch (dimension) {
            case OVERWORLD -> setting.overworld();
            case NETHER -> Objects.requireNonNull(setting.dimensions().get(LevelStem.NETHER)).generator();
        };
    }

    public static @NotNull RegistryAccess.Frozen getRegistryAccess() {
        return getMinecraftServer().registryAccess();
    }

    private static @NotNull DedicatedServer getMinecraftServer() {
        return ((CraftServer) Bukkit.getServer()).getServer();
    }

    public enum Dimension {
        OVERWORLD, NETHER
    }
}

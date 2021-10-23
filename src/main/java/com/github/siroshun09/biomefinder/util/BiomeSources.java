package com.github.siroshun09.biomefinder.util;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.jetbrains.annotations.NotNull;

public final class BiomeSources {

    public static @NotNull OverworldBiomeSource getOverworldSource(long seed, boolean large) {
        return new OverworldBiomeSource(seed, false, large, getRegistry());
    }

    public static @NotNull Registry<Biome> getRegistry() {
        return ((CraftServer) Bukkit.getServer()).getServer().registryHolder.registryOrThrow(Registry.BIOME_REGISTRY);
    }
}

package com.github.siroshun09.biomefinder.util;

import net.kyori.adventure.key.Key;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NMSUtils {

    @SuppressWarnings("PatternValidation")
    public static @Nullable Key toBiomeKey(@NotNull Biome biome) {
        var key = getRegistryAccess().registryOrThrow(Registries.BIOME).getKey(biome);
        return key != null ? Key.key(key.getNamespace(), key.getPath()) : null;
    }

    public static @NotNull RegistryAccess.Frozen getRegistryAccess() {
        return MinecraftServer.getServer().registryAccess();
    }
}

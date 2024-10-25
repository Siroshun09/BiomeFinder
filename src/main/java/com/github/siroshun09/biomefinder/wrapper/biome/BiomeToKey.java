package com.github.siroshun09.biomefinder.wrapper.biome;

import com.github.siroshun09.biomefinder.wrapper.registry.RegistryAccessor;
import net.kyori.adventure.key.Key;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BiomeToKey {

    @SuppressWarnings("PatternValidation")
    public static @Nullable Key convert(@NotNull Biome biome) {
        var key = RegistryAccessor.registry().lookupOrThrow(Registries.BIOME).getKey(biome);
        return key != null ? Key.key(key.getNamespace(), key.getPath()) : null;
    }

    private BiomeToKey() {
        throw new UnsupportedOperationException();
    }
}

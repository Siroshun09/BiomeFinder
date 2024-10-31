package com.github.siroshun09.biomefinder.wrapper.biome;

import com.github.siroshun09.biomefinder.wrapper.registry.RegistryAccessor;
import net.kyori.adventure.key.Key;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class BiomeToKey {

    @SuppressWarnings("PatternValidation")
    public static @Nullable Key convert(Biome biome) {
        var key = RegistryAccessor.registry().lookupOrThrow(Registries.BIOME).getKey(biome);
        return key != null ? Key.key(key.getNamespace(), key.getPath()) : null;
    }

    private BiomeToKey() {
        throw new UnsupportedOperationException();
    }
}

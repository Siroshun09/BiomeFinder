package com.github.siroshun09.biomefinder.wrapper.biome;

import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.stream.Stream;

@NullMarked
public interface BiomeSource {

    @Nullable
    Key getBiome(int x, int y, int z);

    Stream<Key> getPossibleBiomes();

}

package com.github.siroshun09.biomefinder.wrapper.biome;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public interface BiomeSource {

    @Nullable Key getBiome(int x, int y, int z);

    @NotNull Stream<Key> getPossibleBiomes();

}

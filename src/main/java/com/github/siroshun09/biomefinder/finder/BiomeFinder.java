package com.github.siroshun09.biomefinder.finder;

import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface BiomeFinder extends Runnable {

    @NotNull Collection<Biome> getFoundBiomes();

    @NotNull Collection<Biome> getPossibleBiomes();

}

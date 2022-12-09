package com.github.siroshun09.biomefinder.finder;

import com.github.siroshun09.biomefinder.util.NMSUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.RandomState;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class MapWalker implements BiomeFinder {

    private final Set<Biome> discoveredBiomes = new HashSet<>();

    private final int originX;
    private final int y;
    private final int originZ;
    private final int distance;
    private final int radius;
    private final long seed;
    private final NMSUtils.Dimension dimension;
    private final boolean large;

    public MapWalker(@NotNull NMSUtils.Dimension dimension, int originX, int y, int originZ, int distance, int radius,
                     long seed, boolean large) {
        this.dimension = dimension;
        this.originX = originX;
        this.y = y;
        this.originZ = originZ;
        this.distance = distance;
        this.radius = radius;
        this.seed = seed;
        this.large = large;
    }

    @Override
    public void run() {
        int minX = originX - radius;
        int minZ = originZ - radius;
        int maxX = originX + radius;
        int maxZ = originZ + radius;

        int biomeY = QuartPos.fromBlock(y);

        var biomeSource = NMSUtils.getBiomeSource(dimension);
        var randomState = RandomState.create(NMSUtils.getNoiseGeneratorSettings(dimension, large), NMSUtils.getNoiseParameters(), seed);

        for (int x = minX; x < maxX; x += distance) {
            for (int z = minZ; z < maxZ; z += distance) {
                var biome = biomeSource.getNoiseBiome(QuartPos.fromBlock(x), biomeY, QuartPos.fromBlock(z), randomState.sampler()).value();
                discoveredBiomes.add(biome);
            }
        }
    }

    @Override
    public @NotNull Collection<Biome> getFoundBiomes() {
        return discoveredBiomes;
    }

    @Override
    public @NotNull Collection<Biome> getPossibleBiomes() {
        return NMSUtils.getBiomeSource(dimension).possibleBiomes().stream().map(Holder::value).toList();
    }
}

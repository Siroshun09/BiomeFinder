package com.github.siroshun09.biomefinder.finder;

import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class MapWalker implements BiomeFinder {

    private final BiomeSource source;
    private final Set<Biome> discoveredBiomes = new HashSet<>();

    private final int originX;
    private final int y;
    private final int originZ;
    private final int distance;
    private final int radius;

    public MapWalker(@NotNull BiomeSource source, int originX, int y, int originZ, int distance, int radius) {
        this.source = source;
        this.originX = originX;
        this.y = y;
        this.originZ = originZ;
        this.distance = distance;
        this.radius = radius;
    }

    @Override
    public void run() {
        int minX = originX - radius;
        int minZ = originZ - radius;
        int maxX = originX + radius;
        int maxZ = originZ + radius;

        int biomeY = QuartPos.fromBlock(y);

        for (int x = minX; x < maxX; x += distance) {
            for (int z = minZ; z < maxZ; z += distance) {
                var biome = source.getNoiseBiome(QuartPos.fromBlock(x), biomeY, QuartPos.fromBlock(z));
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
        return source.possibleBiomes();
    }
}

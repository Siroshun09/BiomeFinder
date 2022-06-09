package com.github.siroshun09.biomefinder.finder;

import com.github.siroshun09.biomefinder.util.NMSUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class MapWalker implements BiomeFinder {

    private final ChunkGenerator source;
    private final Set<Biome> discoveredBiomes = new HashSet<>();

    private final int originX;
    private final int y;
    private final int originZ;
    private final int distance;
    private final int radius;
    private final long seed;

    public MapWalker(@NotNull ChunkGenerator source, int originX, int y, int originZ, int distance, int radius, long seed) {
        this.source = source;
        this.originX = originX;
        this.y = y;
        this.originZ = originZ;
        this.distance = distance;
        this.radius = radius;
        this.seed = seed;
    }

    @Override
    public void run() {
        int minX = originX - radius;
        int minZ = originZ - radius;
        int maxX = originX + radius;
        int maxZ = originZ + radius;

        int biomeY = QuartPos.fromBlock(y);

        var noiseRegistry = NMSUtils.getRegistryAccess().registryOrThrow(Registry.NOISE_REGISTRY);

        // ChunkMap L427-433
        var randomState =
                source instanceof NoiseBasedChunkGenerator noiseBasedChunkGenerator ?
                        RandomState.create(noiseBasedChunkGenerator.generatorSettings().value(), noiseRegistry, seed) :
                        RandomState.create(NoiseGeneratorSettings.dummy(), noiseRegistry, seed);

        for (int x = minX; x < maxX; x += distance) {
            for (int z = minZ; z < maxZ; z += distance) {
                var biome = source.getBiomeSource().getNoiseBiome(QuartPos.fromBlock(x), biomeY, QuartPos.fromBlock(z), randomState.sampler()).value();
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
        return source.getBiomeSource().possibleBiomes().stream().map(Holder::value).toList();
    }
}

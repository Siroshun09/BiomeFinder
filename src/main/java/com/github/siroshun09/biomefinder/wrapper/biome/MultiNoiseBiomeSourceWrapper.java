package com.github.siroshun09.biomefinder.wrapper.biome;

import com.github.siroshun09.biomefinder.wrapper.registry.RegistryAccessor;
import net.kyori.adventure.key.Key;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class MultiNoiseBiomeSourceWrapper implements BiomeSource {

    public static MultiNoiseBiomeSourceWrapper overworld(long seed) {
        return create(MultiNoiseBiomeSourceParameterLists.OVERWORLD, NoiseGeneratorSettings.OVERWORLD, seed);
    }

    public static MultiNoiseBiomeSourceWrapper largeBiomes(long seed) {
        return create(MultiNoiseBiomeSourceParameterLists.OVERWORLD, NoiseGeneratorSettings.LARGE_BIOMES, seed);
    }

    public static MultiNoiseBiomeSourceWrapper nether(long seed) {
        return create(MultiNoiseBiomeSourceParameterLists.NETHER, NoiseGeneratorSettings.NETHER, seed);
    }

    private static MultiNoiseBiomeSourceWrapper create(ResourceKey<MultiNoiseBiomeSourceParameterList> parameterListKey, ResourceKey<NoiseGeneratorSettings> noiseGeneratorSettingsKey, long seed) {
        var registry = RegistryAccessor.registry();
        return new MultiNoiseBiomeSourceWrapper(
                MultiNoiseBiomeSource.createFromPreset(
                        registry.registryOrThrow(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST).getHolderOrThrow(parameterListKey)
                ),
                RandomState.create(
                        registry.lookupOrThrow(Registries.NOISE_SETTINGS).getOrThrow(noiseGeneratorSettingsKey).value(),
                        registry.lookupOrThrow(Registries.NOISE),
                        seed
                )
        );
    }

    private final MultiNoiseBiomeSource biomeSource;
    private final RandomState randomState;

    private MultiNoiseBiomeSourceWrapper(@NotNull MultiNoiseBiomeSource biomeSource, @NotNull RandomState randomState) {
        this.biomeSource = biomeSource;
        this.randomState = randomState;
    }

    @Override
    public @Nullable Key getBiome(int x, int y, int z) {
        var biome = this.biomeSource.getNoiseBiome(QuartPos.fromBlock(x), QuartPos.fromBlock(y), QuartPos.fromBlock(z), this.randomState.sampler());
        return BiomeToKey.convert(biome.value());
    }

    @Override
    public @NotNull Stream<Key> getPossibleBiomes() {
        return this.biomeSource.possibleBiomes().stream().map(Holder::value).map(BiomeToKey::convert);
    }
}

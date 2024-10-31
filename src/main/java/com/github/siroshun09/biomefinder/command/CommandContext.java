package com.github.siroshun09.biomefinder.command;

import com.github.siroshun09.biomefinder.wrapper.BlockPos;
import com.github.siroshun09.biomefinder.wrapper.Dimension;
import com.github.siroshun09.biomefinder.wrapper.biome.BiomeSource;
import com.github.siroshun09.biomefinder.wrapper.biome.MultiNoiseBiomeSourceWrapper;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record CommandContext(long seed, Dimension dimension, boolean large,
                             int radius, int centerX, int centerZ,
                             boolean showAllBiomes, boolean showDiscoveredBiomes) {

    public BiomeSource createBiomeSource() {
        return switch (this.dimension) {
            case OVERWORLD -> {
                if (this.large) {
                    yield MultiNoiseBiomeSourceWrapper.largeBiomes(this.seed);
                } else {
                    yield MultiNoiseBiomeSourceWrapper.overworld(this.seed);
                }
            }
            case NETHER -> MultiNoiseBiomeSourceWrapper.nether(this.seed);
        };
    }

    public BlockPos center() {
        return new BlockPos(this.centerX, 64, this.centerZ);
    }
}

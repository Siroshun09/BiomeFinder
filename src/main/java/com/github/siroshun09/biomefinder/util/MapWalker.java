package com.github.siroshun09.biomefinder.util;

import com.github.siroshun09.biomefinder.wrapper.BlockPos;
import com.github.siroshun09.biomefinder.wrapper.biome.BiomeSource;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiConsumer;

@NullMarked
public record MapWalker(BiomeSource biomeSource, BiConsumer<Key, BlockPos> biomeConsumer) {

    public void walk(BlockPos center, int radius, int distance) {
        int minX = center.x() - radius;
        int minZ = center.z() - radius;
        int maxX = center.x() + radius;
        int maxZ = center.z() + radius;
        int y = center.y();

        for (int x = minX; x < maxX; x += distance) {
            for (int z = minZ; z < maxZ; z += distance) {
                var biomeKey = this.biomeSource.getBiome(x, y, z);

                if (biomeKey != null) {
                    this.biomeConsumer.accept(biomeKey, new BlockPos(x, y, z));
                }
            }
        }
    }

}

package com.github.siroshun09.biomefinder.wrapper;

import org.bukkit.World;
import org.jspecify.annotations.Nullable;

public enum Dimension {

    OVERWORLD,
    NETHER;

    public static @Nullable Dimension fromBukkit(World.Environment env) {
        return switch (env) {
            case NORMAL -> OVERWORLD;
            case NETHER -> NETHER;
            default -> null;
        };
    }

}

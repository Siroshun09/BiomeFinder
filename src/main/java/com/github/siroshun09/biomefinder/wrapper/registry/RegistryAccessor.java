package com.github.siroshun09.biomefinder.wrapper.registry;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class RegistryAccessor {

    public static RegistryAccess.Frozen registry() {
        return MinecraftServer.getServer().registryAccess();
    }

    private RegistryAccessor() {
        throw new UnsupportedOperationException();
    }
}

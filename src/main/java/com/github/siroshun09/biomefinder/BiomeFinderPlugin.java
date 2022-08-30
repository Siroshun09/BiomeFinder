package com.github.siroshun09.biomefinder;

import com.github.siroshun09.biomefinder.command.AbstractBiomeFinderCommand;
import com.github.siroshun09.biomefinder.command.FindBiomesCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BiomeFinderPlugin extends JavaPlugin {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onEnable() {
        registerCommand(new FindBiomesCommand(executor), "findbiomes");
    }

    @Override
    public void onDisable() {
        executor.shutdownNow();
    }

    private void registerCommand(@NotNull AbstractBiomeFinderCommand commandImpl, @NotNull String commandName) {
        var pluginCommand = Optional.ofNullable(getCommand(commandName)).orElseThrow();
        pluginCommand.setExecutor(commandImpl);
        pluginCommand.setTabCompleter(commandImpl);
    }
}

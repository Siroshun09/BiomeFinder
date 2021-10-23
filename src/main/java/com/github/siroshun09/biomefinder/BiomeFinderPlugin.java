package com.github.siroshun09.biomefinder;

import com.github.siroshun09.biomefinder.command.BiomeFinderCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BiomeFinderPlugin extends JavaPlugin {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onEnable() {
        var pluginCommand = Optional.ofNullable(getCommand("findbiomes")).orElseThrow();
        var command = new BiomeFinderCommand(executor);

        pluginCommand.setExecutor(command);
        pluginCommand.setTabCompleter(command);
    }

    @Override
    public void onDisable() {
        executor.shutdownNow();
    }
}

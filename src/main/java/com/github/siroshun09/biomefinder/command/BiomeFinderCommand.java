package com.github.siroshun09.biomefinder.command;

import com.github.siroshun09.biomefinder.finder.BiomeFinder;
import com.github.siroshun09.biomefinder.finder.MapWalker;
import com.github.siroshun09.biomefinder.util.BiomeSources;
import com.google.common.base.Stopwatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BiomeFinderCommand implements CommandExecutor, TabCompleter {

    private final ExecutorService executor;
    private CompletableFuture<?> currentTask;

    public BiomeFinderCommand(@NotNull ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be executed by the player in game.");
            return true;
        }

        if (!sender.hasPermission("biomefinder.command")) {
            sender.sendMessage("You don't have the permission: biomefinder.command");
            return true;
        }

        if (currentTask != null && !currentTask.isDone()) {
            sender.sendMessage("The biome search task is already running.");
            return true;
        }

        int radius = 500;


        if (0 < args.length) {
            try {
                radius = Integer.parseInt(args[0]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(args[0] + " is not valid number.");
                return true;
            }
        }

        boolean showFoundBiomes;

        if (1 < args.length) {
            showFoundBiomes = Boolean.parseBoolean(args[1]);
        } else {
            showFoundBiomes = true;
        }

        sender.sendMessage("Searching for biomes within " + radius + " blocks...");

        var loc = player.getLocation();
        var finder = new MapWalker(
                BiomeSources.getOverworldSource(player.getWorld().getSeed(), false), // TODO: large biomes
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 16, radius
        );

        var stopwatch = Stopwatch.createStarted();

        currentTask =
                CompletableFuture.runAsync(finder, executor)
                        .thenRunAsync(() -> sendResult(sender, finder, showFoundBiomes))
                        .thenRunAsync(() -> sender.sendMessage("Done! (" + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + "ms)"));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }

    private void sendResult(@NotNull CommandSender sender, @NotNull BiomeFinder finder, boolean showFoundBiomes) {
        Collection<Biome> biomes;

        if (showFoundBiomes) {
            biomes = finder.getFoundBiomes();
        } else {
            var tempList = new ArrayList<>(finder.getPossibleBiomes());

            for (var found : finder.getFoundBiomes()) {
                tempList.remove(found);
            }

            biomes = tempList;
        }

        sender.sendMessage(showFoundBiomes ? "Discovered biomes: " : "Undiscovered biomes: ");

        sender.sendMessage(
                biomes.stream()
                        .map(BiomeSources.getRegistry()::getKey)
                        .map(Optional::ofNullable)
                        .map(optional -> optional.map(ResourceLocation::toString))
                        .map(optional -> optional.orElse("unknown_biome_name"))
                        .collect(Collectors.joining(", "))
        );
    }
}

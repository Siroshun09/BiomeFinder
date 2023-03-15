package com.github.siroshun09.biomefinder.command;

import com.github.siroshun09.biomefinder.finder.BiomeFinder;
import com.github.siroshun09.biomefinder.finder.MapWalker;
import com.github.siroshun09.biomefinder.util.NMSUtils;
import com.google.common.base.Stopwatch;
import net.kyori.adventure.text.Component;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static com.github.siroshun09.biomefinder.message.CommandMessages.ALL_BIOME_LIST;
import static com.github.siroshun09.biomefinder.message.CommandMessages.ALL_BIOME_LIST_HEADER;
import static com.github.siroshun09.biomefinder.message.CommandMessages.BIOME_LIST;
import static com.github.siroshun09.biomefinder.message.CommandMessages.COMMAND_CONTEXT;
import static com.github.siroshun09.biomefinder.message.CommandMessages.DISCOVERED_BIOMES;
import static com.github.siroshun09.biomefinder.message.CommandMessages.FIND_BIOMES_HELP;
import static com.github.siroshun09.biomefinder.message.CommandMessages.FINISH_SEARCHING;
import static com.github.siroshun09.biomefinder.message.CommandMessages.FOUND_BIOME;
import static com.github.siroshun09.biomefinder.message.CommandMessages.NOT_FOUND_BIOME;
import static com.github.siroshun09.biomefinder.message.CommandMessages.START_SEARCHING;
import static com.github.siroshun09.biomefinder.message.CommandMessages.UNDISCOVERED_BIOMES;

public class FindBiomesCommand extends AbstractBiomeFinderCommand {

    public FindBiomesCommand(@NotNull Executor executor) {
        super("biomefinder.command.findbiomes", executor);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!preProcess(sender, args, FIND_BIOMES_HELP)) {
            return true;
        }

        var context = parseArgument(sender, args);

        sender.sendMessage(COMMAND_CONTEXT.apply(context));

        sender.sendMessage(START_SEARCHING);

        var finder = new MapWalker(
                context.dimension(),
                context.centerX(), 64, context.centerZ(),
                16, context.radius(),
                context.seed(), context.large()
        );

        var stopwatch = Stopwatch.createStarted();

        var executor = getExecutor();
        setCurrentTask(
                CompletableFuture.runAsync(finder, executor)
                        .thenRunAsync(() -> sendResult(sender, finder, context.showAllBiomes(), context.showDiscoveredBiomes()), executor)
                        .thenRunAsync(() -> sender.sendMessage(FINISH_SEARCHING.apply(stopwatch)), executor)
                        .thenRunAsync(() -> setCurrentTask(null))
        );

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }

    private void sendResult(@NotNull CommandSender sender, @NotNull BiomeFinder finder,
                            boolean showAllBiomes, boolean showFoundBiomes) {
        if (showAllBiomes) {
            var components = new ArrayList<Component>();

            for (var biome : finder.getPossibleBiomes()) {
                var biomeKey = NMSUtils.toBiomeKey(biome);

                if (biomeKey == null) {
                    continue;
                }

                if (finder.getFoundBiomes().contains(biome)) {
                    components.add(FOUND_BIOME.apply(biomeKey));
                } else {
                    components.add(NOT_FOUND_BIOME.apply(biomeKey));
                }
            }

            sender.sendMessage(ALL_BIOME_LIST_HEADER);
            sender.sendMessage(ALL_BIOME_LIST.apply(components));
            return;
        }

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

        sender.sendMessage(showFoundBiomes ? DISCOVERED_BIOMES : UNDISCOVERED_BIOMES);

        var biomeKeys =
                biomes.stream()
                        .map(NMSUtils::toBiomeKey)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        sender.sendMessage(BIOME_LIST.apply(biomeKeys));
    }


    private @NotNull CommandContext parseArgument(@NotNull CommandSender sender, @NotNull String[] args) {
        Long seed = null;
        NMSUtils.Dimension dimension = NMSUtils.Dimension.OVERWORLD;
        boolean large = false;
        int radius = 500;
        Integer centerX = null, centerZ = null;
        boolean showAllBiomes = false;
        boolean showDiscoveredBiomes = true;

        ArgumentType argumentType = null;

        for (var arg : args) {
            if (argumentType == null) {
                argumentType =
                        switch (arg) {
                            case "-s", "--seed" -> ArgumentType.SEED;
                            case "-d", "--dimension" -> ArgumentType.DIMENSION;
                            case "-l", "--large" -> {
                                large = true;
                                yield null;
                            }
                            case "-r", "--radius" -> ArgumentType.RADIUS;
                            case "-x", "--center-x" -> ArgumentType.X;
                            case "-z", "--center-z" -> ArgumentType.Z;
                            case "-sab", "--show-all-biomes" -> {
                                showAllBiomes = true;
                                yield null;
                            }
                            case "-sdb", "--show-discovered-biomes" -> ArgumentType.SHOW_DISCOVERED_BIOMES;
                            case "-cl", "--current-location" -> {
                                if (sender instanceof Player player) {
                                    centerX = player.getLocation().getBlockX();
                                    centerZ = player.getLocation().getBlockZ();
                                }

                                yield null;
                            }
                            case "-cw", "--current-world" -> {
                                if (sender instanceof Player player) {
                                    var world = player.getWorld();
                                    seed = world.getSeed();

                                    if (centerX == null) {
                                        centerX = world.getSpawnLocation().getBlockX();
                                    }

                                    if (centerZ == null) {
                                        centerZ = world.getSpawnLocation().getBlockZ();
                                    }

                                    dimension = world.getEnvironment() != World.Environment.NETHER ? NMSUtils.Dimension.OVERWORLD : NMSUtils.Dimension.NETHER;
                                }

                                yield null;
                            }
                            case "-w", "--world" -> ArgumentType.WORLD;
                            default -> null;
                        };
            } else {
                switch (argumentType) {
                    case SEED -> seed = parseToSeed(arg);
                    case DIMENSION -> {
                        if (arg.equalsIgnoreCase("overworld")) {
                            dimension = NMSUtils.Dimension.OVERWORLD;
                        } else if (arg.equalsIgnoreCase("nether")) {
                            dimension = NMSUtils.Dimension.NETHER;
                        }
                    }
                    case RADIUS -> radius = parseInt(arg, 500);
                    case X -> centerX = parseInt(arg, 0);
                    case Z -> centerZ = parseInt(arg, 0);
                    case SHOW_DISCOVERED_BIOMES -> showDiscoveredBiomes = Boolean.parseBoolean(arg);
                    case WORLD -> {
                        var world = Bukkit.getWorld(arg);

                        if (world != null) {
                            seed = world.getSeed();
                            centerX = world.getSpawnLocation().getBlockX();
                            centerZ = world.getSpawnLocation().getBlockZ();
                        }
                    }
                }

                argumentType = null;
            }
        }

        if (seed == null) {
            seed = new Random().nextLong();
        }

        if (centerX == null) {
            centerX = 0;
        }

        if (centerZ == null) {
            centerZ = 0;
        }

        return new CommandContext(
                seed, dimension, large,
                Math.abs(radius), centerX, centerZ,
                showAllBiomes, showDiscoveredBiomes
        );
    }

    private int parseInt(@NotNull String original, int def) {
        try {
            return Integer.parseInt(original);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private long parseToSeed(@NotNull String original) {
        try {
            return Long.parseLong(original);
        } catch (NumberFormatException ignored) {
            return original.hashCode();
        }
    }

    private enum ArgumentType {
        SEED, DIMENSION, RADIUS, X, Z, SHOW_DISCOVERED_BIOMES, WORLD,
    }
}

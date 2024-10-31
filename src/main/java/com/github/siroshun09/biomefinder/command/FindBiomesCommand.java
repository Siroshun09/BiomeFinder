package com.github.siroshun09.biomefinder.command;

import com.github.siroshun09.biomefinder.util.MapWalker;
import com.github.siroshun09.biomefinder.wrapper.Dimension;
import com.github.siroshun09.biomefinder.wrapper.biome.BiomeSource;
import com.google.common.base.Stopwatch;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

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

@NullMarked
public class FindBiomesCommand extends AbstractBiomeFinderCommand {

    public FindBiomesCommand() {
        super("biomefinder.command.findbiomes", FIND_BIOMES_HELP);
    }

    @Override
    protected void run(CommandSender sender, String[] args) {
        var context = this.parseArgument(sender, args);

        sender.sendMessage(COMMAND_CONTEXT.apply(context));

        var biomeSource = context.createBiomeSource();
        var foundBiomes = new HashSet<Key>();
        var mapWalker = new MapWalker(biomeSource, (biome, pos) -> foundBiomes.add(biome));

        sender.sendMessage(START_SEARCHING);

        var stopwatch = Stopwatch.createStarted();
        mapWalker.walk(context.center(), context.radius(), 16);
        this.sendResult(sender, biomeSource, foundBiomes, context.showAllBiomes(), context.showDiscoveredBiomes());

        sender.sendMessage(FINISH_SEARCHING.apply(stopwatch.stop()));
    }

    private void sendResult(CommandSender sender, BiomeSource biomeSource, Set<Key> foundBiomes,
                            boolean showAllBiomes, boolean showFoundBiomes) {
        if (showAllBiomes) {
            showAllBiomes(sender, biomeSource, foundBiomes);
        } else if (showFoundBiomes) {
            showBiomes(sender, DISCOVERED_BIOMES, foundBiomes.stream());
        } else {
            showBiomes(sender, UNDISCOVERED_BIOMES, biomeSource.getPossibleBiomes().filter(Predicate.not(foundBiomes::contains)));
        }
    }

    private static void showAllBiomes(CommandSender sender, BiomeSource biomeSource, Set<Key> foundBiomes) {
        var biomes = biomeSource.getPossibleBiomes().map(biome -> {
            if (foundBiomes.contains(biome)) {
                return FOUND_BIOME.apply(biome.asString());
            } else {
                return NOT_FOUND_BIOME.apply(biome.asString());
            }
        }).toList();

        sender.sendMessage(ALL_BIOME_LIST_HEADER);
        sender.sendMessage(ALL_BIOME_LIST.apply(biomes));
    }

    private static void showBiomes(CommandSender sender, Component header, Stream<Key> biomeKeyStream) {
        sender.sendMessage(header);
        sender.sendMessage(BIOME_LIST.apply(biomeKeyStream.map(Key::asString).toList()));
    }

    private CommandContext parseArgument(CommandSender sender, String[] args) {
        Long seed = null;
        Dimension dimension = null;
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

                                dimension = Dimension.fromBukkit(world.getEnvironment());
                            }

                            yield null;
                        }
                        case "-w", "--world" -> ArgumentType.WORLD;
                        default -> null;
                    };
            } else {
                switch (argumentType) {
                    case SEED -> seed = this.parseToSeed(arg);
                    case DIMENSION -> {
                        if (arg.equalsIgnoreCase("overworld")) {
                            dimension = Dimension.OVERWORLD;
                        } else if (arg.equalsIgnoreCase("nether")) {
                            dimension = Dimension.NETHER;
                        }
                    }
                    case RADIUS -> radius = this.parseInt(arg, 500);
                    case X -> centerX = this.parseInt(arg, 0);
                    case Z -> centerZ = this.parseInt(arg, 0);
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

        if (dimension == null) {
            dimension = Dimension.OVERWORLD;
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

    private int parseInt(String original, int def) {
        try {
            return Integer.parseInt(original);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private long parseToSeed(String original) {
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

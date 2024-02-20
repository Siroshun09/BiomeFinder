package com.github.siroshun09.biomefinder.command;

import com.github.siroshun09.biomefinder.util.SeedGenerator;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.github.siroshun09.biomefinder.message.CommandMessages.GENERATED_SEED;
import static com.github.siroshun09.biomefinder.message.CommandMessages.GENERATE_SEED_HELP;
import static com.github.siroshun09.biomefinder.message.CommandMessages.INVALID_BIOME;
import static com.github.siroshun09.biomefinder.message.CommandMessages.SEED_NOT_FOUND;
import static com.github.siroshun09.biomefinder.message.CommandMessages.START_GENERATING_SEED;

public class GenerateSeedCommand extends AbstractBiomeFinderCommand {

    private static final String PERMISSION = "biomefinder.command";

    public GenerateSeedCommand(@NotNull Executor executor) {
        super("biomefinder.command.generateseed", executor);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!preProcess(sender, args, GENERATE_SEED_HELP)) {
            return true;
        }

        Key key;

        if (args.length != 0) {
            if (Key.parseable(args[0])) {
                //noinspection PatternValidation
                key = Key.key(args[0]);
            } else {
                sender.sendMessage(INVALID_BIOME.apply(args[0]));
                return true;
            }
        } else {
            key = Key.key("minecraft:plains");
        }

        if (Registry.BIOME.get(new NamespacedKey(key.namespace(), key.value())) == null) {
            sender.sendMessage(INVALID_BIOME.apply(key.asString()));
            return true;
        }

        sender.sendMessage(START_GENERATING_SEED.apply(key));

        var executor = getExecutor();
        setCurrentTask(
                CompletableFuture
                        .supplyAsync(() -> SeedGenerator.generateSeedWithFixedSpawnBiome(key), executor)
                        .thenAcceptAsync(seed -> {
                            if (seed != -1) {
                                sender.sendMessage(GENERATED_SEED.apply(seed));
                            } else {
                                sender.sendMessage(SEED_NOT_FOUND);
                            }
                        }, executor)
                        .thenRunAsync(() -> setCurrentTask(null))
        );

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1 || !sender.hasPermission(PERMISSION)) {
            return Collections.emptyList();
        }

        return Registry.BIOME.stream()
                .map(Biome::getKey)
                .filter(key -> key.asString().startsWith(args[0]) || key.asMinimalString().startsWith(args[0]))
                .map(Key::asString)
                .toList();
    }
}

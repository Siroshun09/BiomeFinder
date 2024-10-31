package com.github.siroshun09.biomefinder.command;

import com.github.siroshun09.biomefinder.util.SeedGenerator;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Collections;

import static com.github.siroshun09.biomefinder.message.CommandMessages.GENERATED_SEED;
import static com.github.siroshun09.biomefinder.message.CommandMessages.GENERATE_SEED_HELP;
import static com.github.siroshun09.biomefinder.message.CommandMessages.INVALID_BIOME;
import static com.github.siroshun09.biomefinder.message.CommandMessages.SEED_NOT_FOUND;
import static com.github.siroshun09.biomefinder.message.CommandMessages.START_GENERATING_SEED;

@NullMarked
public class GenerateSeedCommand extends AbstractBiomeFinderCommand {

    private static final String PERMISSION = "biomefinder.command";

    public GenerateSeedCommand() {
        super("biomefinder.command.generateseed", GENERATE_SEED_HELP);
    }

    @Override
    protected void run(CommandSender sender, String[] args) {
        Key key;

        if (args.length != 0) {
            if (Key.parseable(args[0])) {
                //noinspection PatternValidation
                key = Key.key(args[0]);
            } else {
                sender.sendMessage(INVALID_BIOME.apply(args[0]));
                return;
            }
        } else {
            key = Key.key("minecraft:plains");
        }

        if (Registry.BIOME.get(new NamespacedKey(key.namespace(), key.value())) == null) {
            sender.sendMessage(INVALID_BIOME.apply(key.asString()));
            return;
        }

        sender.sendMessage(START_GENERATING_SEED.apply(key));

        long seed = SeedGenerator.generateSeedWithFixedSpawnBiome(key);
        sender.sendMessage(seed == -1 ? SEED_NOT_FOUND : GENERATED_SEED.apply(seed));
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        if (args.length != 1 || !source.getSender().hasPermission(PERMISSION)) {
            return Collections.emptyList();
        }

        return Registry.BIOME.stream()
            .map(Biome::getKey)
            .filter(key -> key.asString().startsWith(args[0]) || key.asMinimalString().startsWith(args[0]))
            .map(Key::asString)
            .toList();
    }
}

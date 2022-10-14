package com.github.siroshun09.biomefinder.message;

import com.github.siroshun09.biomefinder.command.CommandContext;
import com.github.siroshun09.translationloader.argument.SingleArgument;
import com.google.common.base.Stopwatch;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.event.ClickEvent.copyToClipboard;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public final class CommandMessages {

    public static final Component FIND_BIOMES_HELP =
            text().append(aqua("/findbiomes {arguments}")).append(newline())
                    .append(newline())
                    .append(gray("Arguments:")).append(newline())
                    .append(argument("-s", "--seed", "<seed>", "Specifies the seed value"))
                    .append(argument("-d", "--dimension", "<overworld/nether>", "Specifies the dimension"))
                    .append(argument("-l", "--large", "", "Enables large biomes"))
                    .append(argument("-r", "--radius", "<radius>", "Sets the radius"))
                    .append(argument("-x", "--center-x", "<x>", "Sets the center x"))
                    .append(argument("-z", "--center-z", "<z>", "Sets the center z"))
                    .append(argument("-cl", "--current-location", "", "Sets the center x and z to the current location (player only)"))
                    .append(argument("-cw", "--current-world", "", "Searching this world (player only)"))
                    .append(argument("-w", "--world", "<world name>", "Searching specified world"))
                    .append(argument("-sab", "--show-all-biomes", "", "Shows all biomes"))
                    .append(argument("-sdb", "--show-discovered-biomes", "<true/false>", "Shows found or not found biomes"))
                    .append(newline())
                    .append(gray("Example 1: ").append(aqua("/fb -s 1234 -r 1000")))
                    .append(newline())
                    .append(gray("Example 2: ").append(aqua("/fb -cw -x 0 -z 0")))
                    .append(newline())
                    .append(gray("Example 3: ").append(aqua("/fb -w world -l")))
                    .build();

    public static final SingleArgument<String> ERROR_NO_PERMISSION =
            permission ->
                    text().append(red("You don't have the permission"))
                            .append(gray(": "))
                            .append(aqua(permission))
                            .build();

    public static final Component ERROR_ALREADY_RUNNING = red("The biome search task is already running.");

    public static final SingleArgument<CommandContext> COMMAND_CONTEXT =
            context ->
                    text().append(gray("Seed: ").append(aqua(context.seed()).clickEvent(copyToClipboard(Long.toString(context.seed())))))
                            .append(newline())
                            .append(gray("Dimension: ").append(aqua(context.dimension().name().toLowerCase(Locale.ENGLISH))))
                            .append(newline())
                            .append(gray("Large Biome: ").append(aqua(context.large())))
                            .append(newline())
                            .append(gray("Radius: ").append(aqua(context.radius())).append(space()).append(gray("blocks")))
                            .append(newline())
                            .append(gray("Center (X, Z): ").append(aqua(context.centerX())).append(gray(", ")).append(aqua(context.centerZ())))
                            .build();

    public static final Component START_SEARCHING = gray("Searching for biomes...");

    public static final SingleArgument<Stopwatch> FINISH_SEARCHING =
            stopwatch ->
                    text().append(gray("Done! ("))
                            .append(aqua(stopwatch.elapsed(TimeUnit.MILLISECONDS)))
                            .append(aqua("ms"))
                            .append(gray(")"))
                            .build();

    public static final Component DISCOVERED_BIOMES = gray("Discovered biomes:");

    public static final Component UNDISCOVERED_BIOMES = gray("Undiscovered biomes:");

    public static final SingleArgument<List<String>> BIOME_LIST =
            biomeKeys -> {
                var components =
                        biomeKeys.stream()
                                .map(biomeKey -> biome(biomeKey, AQUA)) // replace to correct key
                                .toArray(Component[]::new);

                var separator = gray(", ");

                return join(JoinConfiguration.separator(separator), components);
            };

    public static final Component ALL_BIOME_LIST_HEADER =
            gray("Result: (")
                    .append(aqua("found"))
                    .append(gray(" / "))
                    .append(red("not found"))
                    .append(gray(")"));

    public static final SingleArgument<List<Component>> ALL_BIOME_LIST =
            biomes -> join(JoinConfiguration.separator(gray(", ")), biomes);

    public static final SingleArgument<String> FOUND_BIOME = biomeKey -> biome(biomeKey, AQUA);

    public static final SingleArgument<String> NOT_FOUND_BIOME = biomeKey -> biome(biomeKey, RED);

    public static final Component GENERATE_SEED_HELP = gray("/generateseed {biome name} (alias: /gs)");

    public static final SingleArgument<String> INVALID_BIOME = invalid -> red("Invalid biome: ").append(aqua(invalid));

    public static final SingleArgument<Biome> START_GENERATING_SEED =
            spawnBiome -> gray("Generating seed with fixed spawn biome: ").append(biome(spawnBiome));

    public static final SingleArgument<Long> GENERATED_SEED =
            seed -> gray("Generated seed (-1 means that the seed is not found, try again): ").append(text(seed, AQUA).clickEvent(copyToClipboard(Long.toString(seed))));

    public static final Component SEED_NOT_FOUND = red("The seed is not found. Try again or change the spawn biome.");

    private static @NotNull Component argument(@NotNull String shortArg, @NotNull String arg,
                                               @NotNull String value, @NotNull String description) {
        var builder = text()
                .append(space())
                .append(aqua(shortArg))
                .append(gray(" (").append(aqua(arg)).append(gray(")")));

        if (!value.isEmpty()) {
            builder.append(space()).append(aqua(value));
        }

        builder.append(gray(" - ")).append(gray(description)).append(newline());

        return builder.build();
    }

    private static @NotNull Component gray(@NotNull String str) {
        return text(str, GRAY);
    }

    private static @NotNull Component red(@NotNull String str) {
        return text(str, RED);
    }

    private static @NotNull Component aqua(@NotNull String str) {
        return text(str, AQUA);
    }

    private static @NotNull Component aqua(long num) {
        return text(num, AQUA);
    }

    private static @NotNull Component aqua(boolean bool) {
        return text(bool, AQUA);
    }

    private static @NotNull String toBiomeTranslationKey(@NotNull String biomeKey) {
        return "biome." + biomeKey.replace(':', '.');
    }

    private static @NotNull Component biome(@NotNull String biomeKey, @NotNull TextColor color) {
        return translatable(toBiomeTranslationKey(biomeKey), color)
                .hoverEvent(HoverEvent.showText(text(biomeKey, WHITE)));
    }

    private static @NotNull Component biome(@NotNull Biome biome) {
        return translatable(biome.translationKey(), AQUA)
                .hoverEvent(HoverEvent.showText(text(biome.getKey().asString(), WHITE)));
    }

    private CommandMessages() {
        throw new UnsupportedOperationException();
    }
}

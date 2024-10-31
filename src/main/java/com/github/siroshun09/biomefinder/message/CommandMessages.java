package com.github.siroshun09.biomefinder.message;

import com.github.siroshun09.biomefinder.command.CommandContext;
import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NullMarked;

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

@NullMarked
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

    public static final Function<String, Component> ERROR_NO_PERMISSION =
        permission ->
            text().append(red("You don't have the permission"))
                .append(gray(": "))
                .append(aqua(permission))
                .build();

    public static final Component ERROR_ALREADY_RUNNING = red("The biome search task is already running.");

    public static final Function<CommandContext, Component> COMMAND_CONTEXT =
        context ->
            text().append(gray("Seed: ").append(seed(context.seed())))
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

    public static final Function<Stopwatch, Component> FINISH_SEARCHING =
        stopwatch ->
            text().append(gray("Done! ("))
                .append(aqua(stopwatch.elapsed(TimeUnit.MILLISECONDS)))
                .append(aqua("ms"))
                .append(gray(")"))
                .build();

    public static final Component DISCOVERED_BIOMES = gray("Discovered biomes:");

    public static final Component UNDISCOVERED_BIOMES = gray("Undiscovered biomes:");

    public static final Function<List<String>, Component> BIOME_LIST =
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

    public static final Function<List<Component>, Component> ALL_BIOME_LIST =
        biomes -> join(JoinConfiguration.separator(gray(", ")), biomes);

    public static final Function<String, Component> FOUND_BIOME = biomeKey -> biome(biomeKey, AQUA);

    public static final Function<String, Component> NOT_FOUND_BIOME = biomeKey -> biome(biomeKey, RED);

    public static final Component GENERATE_SEED_HELP = gray("/generateseed {biome name} (alias: /gs)");

    public static final Function<String, Component> INVALID_BIOME = invalid -> red("Invalid biome: ").append(aqua(invalid));

    public static final Function<Key, Component> START_GENERATING_SEED =
        spawnBiome -> gray("Generating seed with fixed spawn biome: ").append(biome(spawnBiome.asString(), AQUA));

    public static final Function<Long, Component> GENERATED_SEED =
        seed -> gray("Generated seed (-1 means that the seed is not found, try again): ").append(seed(seed));

    public static final Component SEED_NOT_FOUND = red("The seed is not found. Try again or change the spawn biome.");

    private static Component argument(String shortArg, String arg, String value, String description) {
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

    private static Component gray(String str) {
        return text(str, GRAY);
    }

    private static Component red(String str) {
        return text(str, RED);
    }

    private static Component aqua(String str) {
        return text(str, AQUA);
    }

    private static Component aqua(long num) {
        return text(num, AQUA);
    }

    private static Component aqua(boolean bool) {
        return text(bool, AQUA);
    }

    private static Component white(String str) {
        return text(str, WHITE);
    }

    private static String toBiomeTranslationKey(String biomeKey) {
        return "biome." + biomeKey.replace(':', '.');
    }

    private static Component biome(String biomeKey, TextColor color) {
        return translatable(toBiomeTranslationKey(biomeKey), color)
            .hoverEvent(HoverEvent.showText(white(biomeKey)));
    }

    private static Component seed(long seed) {
        return aqua(seed)
            .clickEvent(copyToClipboard(Long.toString(seed)))
            .hoverEvent(HoverEvent.showText(white("Click to copy")));
    }

    private CommandMessages() {
        throw new UnsupportedOperationException();
    }
}

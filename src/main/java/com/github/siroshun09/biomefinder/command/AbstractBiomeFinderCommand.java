package com.github.siroshun09.biomefinder.command;

import com.github.siroshun09.biomefinder.BiomeFinderPlugin;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.siroshun09.biomefinder.message.CommandMessages.ERROR_ALREADY_RUNNING;
import static com.github.siroshun09.biomefinder.message.CommandMessages.ERROR_NO_PERMISSION;

public abstract class AbstractBiomeFinderCommand implements BasicCommand {

    private final String permission;
    private final Component help;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    protected AbstractBiomeFinderCommand(@NotNull String permission, @NotNull Component help) {
        this.permission = permission;
        this.help = help;
    }

    @Override
    public void execute(@NotNull CommandSourceStack source, @NotNull String @NotNull [] args) {
        var sender = source.getSender();

        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ERROR_NO_PERMISSION.apply(permission));
            return;
        }

        if (0 < args.length && args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(help);
            return;
        }

        if (isRunning.compareAndExchange(false, true)) {
            sender.sendMessage(ERROR_ALREADY_RUNNING);
            return;
        }

        Bukkit.getAsyncScheduler().runNow(
                JavaPlugin.getPlugin(BiomeFinderPlugin.class),
                ignored -> {
                    try {
                        run(sender, args);
                    } finally {
                        isRunning.set(false);
                    }
                }
        );
    }

    protected abstract void run(@NotNull CommandSender sender, @NotNull String @NotNull [] args);
}

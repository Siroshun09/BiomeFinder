package com.github.siroshun09.biomefinder.command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.github.siroshun09.biomefinder.message.CommandMessages.ERROR_ALREADY_RUNNING;
import static com.github.siroshun09.biomefinder.message.CommandMessages.ERROR_NO_PERMISSION;

public abstract class AbstractBiomeFinderCommand implements CommandExecutor, TabCompleter {

    private final String permission;
    private final Executor executor;
    private CompletableFuture<?> currentTask;

    protected AbstractBiomeFinderCommand(@NotNull String permission, @NotNull Executor executor) {
        this.permission = permission;
        this.executor = executor;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean preProcess(@NotNull CommandSender sender, @NotNull String[] args, @NotNull Component help) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ERROR_NO_PERMISSION.apply(permission));
            return false;
        }

        if (0 < args.length && args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(help);
            return false;
        }

        if (currentTask != null && !currentTask.isDone()) {
            sender.sendMessage(ERROR_ALREADY_RUNNING);
            return false;
        }

        return true;
    }

    protected @NotNull Executor getExecutor() {
        return executor;
    }

    protected void setCurrentTask(@Nullable CompletableFuture<?> task) {
        this.currentTask = task;
    }
}

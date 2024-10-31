package com.github.siroshun09.biomefinder;

import com.github.siroshun09.biomefinder.command.FindBiomesCommand;
import com.github.siroshun09.biomefinder.command.GenerateSeedCommand;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class BiomeFinderPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register("findbiomes", List.of("fb"), new FindBiomesCommand());
            event.registrar().register("generateseed", List.of("gs"), new GenerateSeedCommand());
        });
    }

}

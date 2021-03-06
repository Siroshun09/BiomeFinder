package com.github.siroshun09.biomefinder.command;

import com.github.siroshun09.biomefinder.util.NMSUtils;

public record CommandContext(long seed, NMSUtils.Dimension dimension, boolean large,
                             int radius, int centerX, int centerZ,
                             boolean showAllBiomes, boolean showDiscoveredBiomes) {

}

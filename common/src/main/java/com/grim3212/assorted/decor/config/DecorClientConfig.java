package com.grim3212.assorted.decor.config;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;

import java.util.function.Supplier;

public class DecorClientConfig {
    public final Supplier<Double> cageSpinMod;
    public final Supplier<Double> wallpaperWidth;

    public DecorClientConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.CLIENT_ONLY, Constants.MOD_ID + "-client");

        cageSpinMod = builder.defineDouble("cage.cageSpinMod", 3.0D, 1.0D, 20.0D, "This is the modifier for how fast entities spin when displayed inside the Cage.");
        wallpaperWidth = builder.defineDouble("wallpaper.wallpaperWidth", 1.0D, 0.1D, 5.0D, "Set this to determine how much the wallpaper will stick off of the wall.");

        builder.setup();
    }
}

package com.grim3212.assorted.decor.config;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;

import java.util.function.Supplier;

public class DecorCommonConfig {

    public final Supplier<Boolean> colorizerEnabled;
    public final Supplier<Boolean> neonSignEnabled;
    public final Supplier<Boolean> hangeablesEnabled;
    public final Supplier<Boolean> fluroEnabled;
    public final Supplier<Boolean> roadwaysEnabled;
    public final Supplier<Boolean> paintingEnabled;
    public final Supplier<Boolean> decorationsEnabled;
    public final Supplier<Boolean> cageEnabled;
    public final Supplier<Boolean> planterPotEnabled;
    public final Supplier<Boolean> extrasEnabled;

    public final Supplier<Boolean> colorizerConsumeBlock;
    public final Supplier<Integer> colorizerBrushCount;
    public final Supplier<Integer> shapeSmoothness;
    public final Supplier<Boolean> framesBurn;
    public final Supplier<Boolean> dyeFrames;
    public final Supplier<Boolean> wallpapersBurn;
    public final Supplier<Boolean> dyeWallpapers;
    public final Supplier<Boolean> wallpapersCopyDye;
    public final Supplier<Integer> numWallpaperOptions;

    public DecorCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, Constants.MOD_ID + "-common");

        colorizerEnabled = builder.defineBoolean("parts.colorizerEnabled", true, "Set this to true if Colorizer blocks and items should be craftable and visible in the creative tab");
        neonSignEnabled = builder.defineBoolean("parts.neonSignEnabled", true, "Set this to true if Neon Signs should be craftable and visible in the creative tab");
        hangeablesEnabled = builder.defineBoolean("parts.hangeablesEnabled", true, "Set this to true if the Hangeable blocks and items should be craftable and visible in the creative tab");
        fluroEnabled = builder.defineBoolean("parts.fluroEnabled", true, "Set this to true if the Fluro blocks should be craftable and visible in the creative tab");
        roadwaysEnabled = builder.defineBoolean("parts.roadwaysEnabled", true, "Set this to true if Roadway blocks and items should be craftable and visible in the creative tab");
        paintingEnabled = builder.defineBoolean("parts.paintingEnabled", true, "Set this to true if Painting rollers and siding should be craftable and visible in the creative tab");
        decorationsEnabled = builder.defineBoolean("parts.decorationsEnabled", true, "Set this to true if the decoration blocks should be craftable and visible in the creative tab");
        cageEnabled = builder.defineBoolean("parts.cageEnabled", true, "Set this to true if the Cage should be craftable and visible in the creative tab");
        planterPotEnabled = builder.defineBoolean("parts.planterPotEnabled", true, "Set this to true if the Planter pot should be craftable and visible in the creative tab");
        extrasEnabled = builder.defineBoolean("parts.extrasEnabled", true, "Set this to true if the extras (chain link fence and new doors) should be craftable and visible in the creative tab");

        colorizerConsumeBlock = builder.defineBoolean("colorizer.colorizerConsumeBlock", true, "Set this to true if the colorizer brush should consume a block when using them");
        colorizerBrushCount = builder.defineInteger("colorizer.colorizerBrushCount", 16, 1, 400, "Set this to the amount of blocks that a brush will be able to colorize after grabbing a block");
        shapeSmoothness = builder.defineInteger("colorizer.shapeSmoothness", 2, 1, 20, "Set this to determine how smooth all of the different slopes collision boxes should be");

        framesBurn = builder.defineBoolean("frames.framesBurn", true, "Set this to true if you want frames to be able to get burnt");
        dyeFrames = builder.defineBoolean("frames.dyeFrames", true, "Set this to true if you want to be able to dye Frames");

        wallpapersBurn = builder.defineBoolean("wallpaper.wallpapersBurn", true, "Set this to true if you want wallpaper to be able to get burnt");
        dyeWallpapers = builder.defineBoolean("wallpaper.dyeWallpapers", true, "Set this to true if you want to be able to dye wallpaper");
        wallpapersCopyDye = builder.defineBoolean("wallpaper.wallpapersCopyDye", true, "Set this to true if you want wallpaper to be able to copy dye colors from adjacent wallpaper");
        numWallpaperOptions = builder.defineInteger("wallpaper.numWallpaperOptions", 24, 1, 256, "Set this to the amount of wallpapers currently defined on the texture");

        builder.setup();
    }
}

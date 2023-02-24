package com.grim3212.assorted.decor;

import com.grim3212.assorted.lib.config.ConfigBuilder;
import com.grim3212.assorted.lib.config.ConfigGroup;
import com.grim3212.assorted.lib.config.ConfigOption;
import com.grim3212.assorted.lib.config.ConfigType;

public class DecorConfig {

    public static class Common {

        public static final ConfigOption<Boolean> partColorizerEnabled = new ConfigOption<>("partColorizerEnabled", ConfigOption.OptionType.BOOLEAN, true, "Set this to true if Colorizer blocks and items should be craftable and visible in the creative tab");
        public static final ConfigOption<Boolean> partNeonSignEnabled = new ConfigOption<>("partNeonSignEnabled", ConfigOption.OptionType.BOOLEAN, true, "Set this to true if Neon Signs should be craftable and visible in the creative tab");
        public static final ConfigOption<Boolean> partHangeablesEnabled = new ConfigOption<>("partHangeablesEnabled", ConfigOption.OptionType.BOOLEAN, true, "Set this to true if the Hangeable blocks and items should be craftable and visible in the creative tab");
        public static final ConfigOption<Boolean> partFluroEnabled = new ConfigOption<>("partFluroEnabled", ConfigOption.OptionType.BOOLEAN, true, "Set this to true if the Fluro blocks should be craftable and visible in the creative tab");
        public static final ConfigOption<Boolean> partRoadwaysEnabled = new ConfigOption<>("partRoadwaysEnabled", ConfigOption.OptionType.BOOLEAN, true, "Set this to true if Roadway blocks and items should be craftable and visible in the creative tab");
        public static final ConfigOption<Boolean> partPaintingEnabled = new ConfigOption<>("partPaintingEnabled", ConfigOption.OptionType.BOOLEAN, true, "Set this to true if Painting rollers and siding should be craftable and visible in the creative tab");
        public static final ConfigOption<Boolean> partDecorationsEnabled = new ConfigOption<>("partDecorationsEnabled", ConfigOption.OptionType.BOOLEAN, true, "Set this to true if the decoration blocks should be craftable and visible in the creative tab");
        public static final ConfigOption<Boolean> partCageEnabled = new ConfigOption<>("partCageEnabled", ConfigOption.OptionType.BOOLEAN, true, "Set this to true if the Cage should be craftable and visible in the creative tab");
        public static final ConfigOption<Boolean> partPlanterPotEnabled = new ConfigOption<>("partPlanterPotEnabled", ConfigOption.OptionType.BOOLEAN, true, "Set this to true if the Planter pot should be craftable and visible in the creative tab");
        public static final ConfigOption<Boolean> partExtrasEnabled = new ConfigOption<>("partExtrasEnabled", ConfigOption.OptionType.BOOLEAN, true, "Set this to true if the extras (chain link fence and new doors) should be craftable and visible in the creative tab");

        public static final ConfigOption<Boolean> colorizerConsumeBlock = new ConfigOption<>("colorizerConsumeBlock", ConfigOption.OptionType.BOOLEAN, true, "Set this to true if the colorizer brush should consume a block when using them");
        public static final ConfigOption.ConfigOptionInteger shapeSmoothness = new ConfigOption.ConfigOptionInteger("shapeSmoothness", 2, "Set this to determine how smooth all of the different slopes collision boxes should be", 1, 20);
        public static final ConfigOption.ConfigOptionInteger colorizerBrushCount = new ConfigOption.ConfigOptionInteger("colorizerBrushCount", 16, "Set this to the amount of blocks that a brush will be able to colorize after grabbing a block", 1, 400);
        public static final ConfigOption<Boolean> framesBurn = new ConfigOption<>("framesBurn", ConfigOption.OptionType.BOOLEAN, true, "Set this to true if you want frames to be able to get burnt");
        public static final ConfigOption<Boolean> dyeFrames = new ConfigOption<>("dyeFrames", ConfigOption.OptionType.BOOLEAN, true, "Set this to true if you want to be able to dye Frames");
        public static final ConfigOption<Boolean> wallpapersBurn = new ConfigOption<>("wallpapersBurn", ConfigOption.OptionType.BOOLEAN, true, "Set this to true if you want wallpaper to be able to get burnt");
        public static final ConfigOption<Boolean> dyeWallpapers = new ConfigOption<>("dyeWallpapers", ConfigOption.OptionType.BOOLEAN, true, "Set this to true if you want to be able to dye wallpaper");
        public static final ConfigOption<Boolean> wallpapersCopyDye = new ConfigOption<>("wallpapersCopyDye", ConfigOption.OptionType.BOOLEAN, true, "Set this to true if you want wallpaper to be able to copy dye colors from adjacent wallpaper");
        public static final ConfigOption.ConfigOptionInteger numWallpaperOptions = new ConfigOption.ConfigOptionInteger("numWallpaperOptions", 24, "Set this to the amount of wallpapers currently defined on the texture", 1, 256);

        public static final ConfigBuilder COMMON_CONFIG = new ConfigBuilder(ConfigType.COMMON)
                .addGroups(new ConfigGroup("Parts").addOptions(partColorizerEnabled, partNeonSignEnabled, partHangeablesEnabled, partFluroEnabled, partRoadwaysEnabled, partPaintingEnabled, partDecorationsEnabled, partCageEnabled, partPlanterPotEnabled, partExtrasEnabled))
                .addGroups(new ConfigGroup("Colorizer").addOptions(colorizerConsumeBlock, colorizerBrushCount, shapeSmoothness))
                .addGroups(new ConfigGroup("Wallpaper").addOptions(dyeWallpapers, wallpapersBurn, wallpapersCopyDye, numWallpaperOptions))
                .addGroups(new ConfigGroup("Frames").addOptions(dyeFrames, framesBurn));

    }

    public static class Client {
        public static final ConfigOption.ConfigOptionFloat cageSpinMod = new ConfigOption.ConfigOptionFloat("cageSpinMod", 3.0F, "This is the modifier for how fast entities spin when displayed inside the Cage", 1.0F, 20.0F);
        public static final ConfigOption.ConfigOptionFloat wallpaperWidth = new ConfigOption.ConfigOptionFloat("wallpaperWidth", 1.0F, "Set this to determine how much the wallpaper will stick off of the wall", 0.1F, 5.0F);
        public static final ConfigBuilder CLIENT_CONFIG = new ConfigBuilder(ConfigType.CLIENT)
                .addGroups(new ConfigGroup("Wallpaper").addOptions(wallpaperWidth), new ConfigGroup("Cage").addOptions(cageSpinMod));
    }

}

//package com.grim3212.assorted.decor.services;
//
//import net.minecraftforge.common.ForgeConfigSpec;
//import org.apache.commons.lang3.tuple.Pair;
//
//public final class ForgeConfigHelper implements IConfigHelper {
//
//    public static final Common COMMON;
//    public static final ForgeConfigSpec COMMON_SPEC;
//
//    static {
//        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
//        COMMON_SPEC = specPair.getRight();
//        COMMON = specPair.getLeft();
//    }
//
//    private static class Common {
//        public final ForgeConfigSpec.BooleanValue partColorizerEnabled;
//        public final ForgeConfigSpec.BooleanValue partNeonSignEnabled;
//        public final ForgeConfigSpec.BooleanValue partHangeablesEnabled;
//        public final ForgeConfigSpec.BooleanValue partFluroEnabled;
//        public final ForgeConfigSpec.BooleanValue partRoadwaysEnabled;
//        public final ForgeConfigSpec.BooleanValue partPaintingEnabled;
//        public final ForgeConfigSpec.BooleanValue partDecorationsEnabled;
//        public final ForgeConfigSpec.BooleanValue partCageEnabled;
//        public final ForgeConfigSpec.BooleanValue partPlanterPotEnabled;
//        public final ForgeConfigSpec.BooleanValue partExtrasEnabled;
//
//        public final ForgeConfigSpec.BooleanValue dyeFrames;
//        public final ForgeConfigSpec.BooleanValue burnFrames;
//        public final ForgeConfigSpec.BooleanValue dyeWallpaper;
//        public final ForgeConfigSpec.BooleanValue copyDye;
//        public final ForgeConfigSpec.BooleanValue burnWallpaper;
//        public final ForgeConfigSpec.IntValue numWallpapers;
//
//        public final ForgeConfigSpec.BooleanValue consumeBlock;
//        public final ForgeConfigSpec.IntValue smoothness;
//        public final ForgeConfigSpec.IntValue brushChargeCount;
//
//        public Common(ForgeConfigSpec.Builder builder) {
//            builder.push("Parts");
//            partColorizerEnabled = builder.comment("Set this to true if Colorizer blocks and items should be craftable and visible in the creative tab").define("partColorizerEnabled", true);
//            partNeonSignEnabled = builder.comment("Set this to true if Neon Signs should be craftable and visible in the creative tab").define("partNeonSignEnabled", true);
//            partHangeablesEnabled = builder.comment("Set this to true if the Hangeable blocks and items should be craftable and visible in the creative tab").define("partHangeablesEnabled", true);
//            partFluroEnabled = builder.comment("Set this to true if the Fluro blocks should be craftable and visible in the creative tab").define("partFluroEnabled", true);
//            partRoadwaysEnabled = builder.comment("Set this to true if Roadway blocks and items should be craftable and visible in the creative tab").define("partRoadwaysEnabled", true);
//            partPaintingEnabled = builder.comment("Set this to true if Painting rollers and siding should be craftable and visible in the creative tab").define("partPaintingEnabled", true);
//            partDecorationsEnabled = builder.comment("Set this to true if the decoration blocks should be craftable and visible in the creative tab").define("partDecorationsEnabled", true);
//            partCageEnabled = builder.comment("Set this to true if the Cage should be craftable and visible in the creative tab").define("partCageEnabled", true);
//            partPlanterPotEnabled = builder.comment("Set this to true if the Planter pot should be craftable and visible in the creative tab").define("partPlanterPotEnabled", true);
//            partExtrasEnabled = builder.comment("Set this to true if the extras (chain link fence and new doors) should be craftable and visible in the creative tab").define("partExtrasEnabled", true);
//            builder.pop();
//
//            builder.push("Wallpaper");
//            dyeWallpaper = builder.comment("Set this to true if you want to be able to dye wallpaper.").define("dyeWallpaper", true);
//            copyDye = builder.comment("Set this to true if you want wallpaper to be able to copy dye colors from adjacent wallpaper.").define("copyDye", true);
//            burnWallpaper = builder.comment("Set this to true if you want wallpaper to be able to get burnt.").define("burnWallpaper", true);
//            numWallpapers = builder.comment("Set this to the amount of wallpapers currently defined on the texture.").defineInRange("numWallpapers", 24, 1, 256);
//            builder.pop();
//
//            builder.push("Frames");
//            dyeFrames = builder.comment("Set this to true if you want to be able to dye Frames.").define("dyeFrames", true);
//            burnFrames = builder.comment("Set this to true if you want frames to be able to get burnt.").define("burnFrames", true);
//            builder.pop();
//
//            builder.push("Colorizer");
//            brushChargeCount = builder.comment("Set this to the amount of blocks that a brush will be able to colorize after grabbing a block.").defineInRange("brushChargeCount", 16, 1, 400);
//            consumeBlock = builder.comment("Set this to true if the colorizer brush should consume a block when using them").define("consumeBlock", true);
//            smoothness = builder.comment("Set this to determine how smooth all of the different slopes collision boxes should be.").defineInRange("smoothness", 2, 1, 10);
//            builder.pop();
//        }
//    }
//
//    public static final Client CLIENT;
//    public static final ForgeConfigSpec CLIENT_SPEC;
//
//    static {
//        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
//        CLIENT_SPEC = specPair.getRight();
//        CLIENT = specPair.getLeft();
//    }
//
//    private static class Client {
//
//        public final ForgeConfigSpec.DoubleValue widthWallpaper;
//
//        public final ForgeConfigSpec.DoubleValue cageEntitySpinMod;
//
//        public Client(ForgeConfigSpec.Builder builder) {
//            builder.push("Wallpaper");
//            widthWallpaper = builder.comment("Set this to determine how much the wallpaper will stick off of the wall.").defineInRange("widthWallpaper", 1.0D, 0.1D, 5.0D);
//            builder.pop();
//
//            builder.push("Cage");
//            cageEntitySpinMod = builder.comment("This is the modifier for how fast entities spin when displayed inside the Cage.").defineInRange("cageEntitySpinMod", 3.0D, 1.0D, 20.0D);
//            builder.pop();
//        }
//
//    }
//
//    @Override
//    public int getShapeSmoothness() {
//        return COMMON.smoothness.get();
//    }
//
//    @Override
//    public boolean doesColorizerConsumeBlock() {
//        return COMMON.consumeBlock.get();
//    }
//
//    @Override
//    public int colorizerBrushChargeCount() {
//        return COMMON.brushChargeCount.get();
//    }
//
//    @Override
//    public boolean canFramesBurn() {
//        return COMMON.burnFrames.get();
//    }
//
//    @Override
//    public boolean canDyeFrames() {
//        return COMMON.dyeFrames.get();
//    }
//
//    @Override
//    public boolean canDyeWallpapers() {
//        return COMMON.dyeWallpaper.get();
//    }
//
//    @Override
//    public boolean canWallpapersBurn() {
//        return COMMON.burnWallpaper.get();
//    }
//
//    @Override
//    public boolean doWallpapersCopyDye() {
//        return COMMON.copyDye.get();
//    }
//
//    @Override
//    public int numWallpaperOptions() {
//        return COMMON.numWallpapers.get();
//    }
//
//    @Override
//    public boolean partColorizerEnabled() {
//        return COMMON.partCageEnabled.get();
//    }
//
//    @Override
//    public boolean partNeonSignEnabled() {
//        return COMMON.partNeonSignEnabled.get();
//    }
//
//    @Override
//    public boolean partHangeablesEnabled() {
//        return COMMON.partHangeablesEnabled.get();
//    }
//
//    @Override
//    public boolean partFluroEnabled() {
//        return COMMON.partFluroEnabled.get();
//    }
//
//    @Override
//    public boolean partRoadwaysEnabled() {
//        return COMMON.partRoadwaysEnabled.get();
//    }
//
//    @Override
//    public boolean partPaintingEnabled() {
//        return COMMON.partPaintingEnabled.get();
//    }
//
//    @Override
//    public boolean partDecorationsEnabled() {
//        return COMMON.partDecorationsEnabled.get();
//    }
//
//    @Override
//    public boolean partCageEnabled() {
//        return COMMON.partCageEnabled.get();
//    }
//
//    @Override
//    public boolean partPlanterPotEnabled() {
//        return COMMON.partPlanterPotEnabled.get();
//    }
//
//    @Override
//    public boolean partExtrasEnabled() {
//        return COMMON.partExtrasEnabled.get();
//    }
//
//    @Override
//    public float cageSpinModifier() {
//        return CLIENT.cageEntitySpinMod.get().floatValue();
//    }
//
//    @Override
//    public float wallpaperWidth() {
//        return CLIENT.widthWallpaper.get().floatValue();
//    }
//}

package com.grim3212.assorted.decor.common.handler;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public final class DecorConfig {

	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	public static class Common {

		public final ForgeConfigSpec.BooleanValue dyeFrames;
		public final ForgeConfigSpec.BooleanValue burnFrames;
		public final ForgeConfigSpec.BooleanValue dyeWallpaper;
		public final ForgeConfigSpec.BooleanValue copyDye;
		public final ForgeConfigSpec.BooleanValue burnWallpaper;
		public final ForgeConfigSpec.IntValue numWallpapers;

		public Common(ForgeConfigSpec.Builder builder) {
			builder.push("Wallpaper");
			dyeWallpaper = builder.comment("Set this to true if you want to be able to dye wallpaper.").define("dyeWallpaper", true);
			copyDye = builder.comment("Set this to true if you want wallpaper to be able to copy dye colors from adjacent wallpaper.").define("copyDye", true);
			burnWallpaper = builder.comment("Set this to true if you want wallpaper to be able to get burnt.").define("burnWallpaper", true);
			numWallpapers = builder.comment("Set this to the amount of wallpapers currently defined on the texture.").defineInRange("numWallpapers", 24, 1, 256);
			builder.pop();

			builder.push("Frames");
			dyeFrames = builder.comment("Set this to true if you want to be able to dye Frames.").define("dyeFrames", true);
			burnFrames = builder.comment("Set this to true if you want frames to be able to get burnt.").define("burnFrames", true);
			builder.pop();

		}
	}

	public static final Client CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;
	static {
		final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	public static class Client {

		public final ForgeConfigSpec.DoubleValue widthWallpaper;

		public Client(ForgeConfigSpec.Builder builder) {
			builder.push("Wallpaper");
			widthWallpaper = builder.comment("Set this to determine how much the wallpaper will stick off of the wall.").defineInRange("widthWallpaper", 1.0D, 0.1D, 5.0D);
			builder.pop();
		}

	}
}

package com.grim3212.assorted.decor.common.item;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.Maps;
import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.item.FrameItem.FrameMaterial;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DecorItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AssortedDecor.MODID);

	public static final RegistryObject<WallpaperItem> WALLPAPER = register("wallpaper", () -> new WallpaperItem(new Item.Properties().tab(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP)));
	public static final RegistryObject<FrameItem> WOOD_FRAME = register("wood_frame", () -> new FrameItem(FrameMaterial.WOOD, new Item.Properties().tab(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP)));
	public static final RegistryObject<FrameItem> IRON_FRAME = register("iron_frame", () -> new FrameItem(FrameMaterial.IRON, new Item.Properties().tab(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP)));

	public static final RegistryObject<ColorizerBrush> COLORIZER_BRUSH = register("colorizer_brush", () -> new ColorizerBrush(new Item.Properties().tab(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP)));
	public static final RegistryObject<Item> UNFIRED_PLANTER_POT = register("unfired_planter_pot", () -> new Item(new Item.Properties().tab(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP)));
	public static final RegistryObject<Item> UNFIRED_CLAY_DECORATION = register("unfired_clay_decoration", () -> new Item(new Item.Properties().tab(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP)));

	public static final RegistryObject<NeonSignItem> NEON_SIGN = register("neon_sign", () -> new NeonSignItem(new Item.Properties().stacksTo(16).tab(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP)));

	public static final RegistryObject<Item> TARBALL = register("tarball", () -> new Item(new Item.Properties().tab(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP)));
	public static final RegistryObject<AsphaltItem> ASPHALT = register("asphalt", () -> new AsphaltItem(new Item.Properties().tab(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP)));

	public static final RegistryObject<Item> PAINT_ROLLER = register("paint_roller", () -> new Item(new Item.Properties().tab(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP).stacksTo(1)));

	public static final Map<DyeColor, RegistryObject<PaintRollerItem>> PAINT_ROLLER_COLORS = Maps.newHashMap();

	static {
		Arrays.stream(DyeColor.values()).forEach((color) -> PAINT_ROLLER_COLORS.put(color, register("paint_roller_" + color.getName(), () -> new PaintRollerItem(color, new Item.Properties().tab(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP)))));
	}

	private static <T extends Item> RegistryObject<T> register(final String name, final Supplier<T> sup) {
		return ITEMS.register(name, sup);
	}
}

package com.grim3212.assorted.decor.common.item;

import java.util.function.Supplier;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.item.FrameItem.FrameMaterial;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DecorItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AssortedDecor.MODID);

	public static final RegistryObject<WallpaperItem> WALLPAPER = register("wallpaper", () -> new WallpaperItem(new Item.Properties().group(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP)));
	public static final RegistryObject<FrameItem> WOOD_FRAME = register("wood_frame", () -> new FrameItem(FrameMaterial.WOOD, new Item.Properties().group(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP)));
	public static final RegistryObject<FrameItem> IRON_FRAME = register("iron_frame", () -> new FrameItem(FrameMaterial.IRON, new Item.Properties().group(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP)));

	public static final RegistryObject<ColorizerBrush> COLORIZER_BRUSH = register("colorizer_brush", () -> new ColorizerBrush(new Item.Properties().group(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP)));
	public static final RegistryObject<Item> UNFIRED_PLANTER_POT = register("unfired_planter_pot", () -> new Item(new Item.Properties().group(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP)));

	public static final RegistryObject<NeonSignItem> NEON_SIGN = register("neon_sign", () -> new NeonSignItem(new Item.Properties().maxStackSize(16).group(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP)));

	private static <T extends Item> RegistryObject<T> register(final String name, final Supplier<T> sup) {
		return ITEMS.register(name, sup);
	}
}

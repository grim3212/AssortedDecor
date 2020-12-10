package com.grim3212.assorted.decor.common.lib;

import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class DecorTags {

	public static class Items {
		public static final IOptionalNamedTag<Item> BUCKETS_WATER = forgeTag("buckets/water");
		public static final IOptionalNamedTag<Item> INGOTS_ALUMINUM = forgeTag("ingots/aluminum");

		private static IOptionalNamedTag<Item> forgeTag(String name) {
			return ItemTags.createOptional(new ResourceLocation("forge", name));
		}
	}
}

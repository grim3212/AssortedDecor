package com.grim3212.assorted.decor.common.lib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class DecorTags {

	public static class Items {
		public static final TagKey<Item> BUCKETS_WATER = forgeTag("buckets/water");
		public static final TagKey<Item> INGOTS_ALUMINUM = forgeTag("ingots/aluminum");

		private static TagKey<Item> forgeTag(String name) {
			return ItemTags.create(new ResourceLocation("forge", name));
		}
	}
}

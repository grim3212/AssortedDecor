package com.grim3212.assorted.decor.common.lib;

import com.grim3212.assorted.decor.AssortedDecor;

import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class DecorTags {

	public static class Items {
		public static final IOptionalNamedTag<Item> BUCKETS_WATER = forgeTag("buckets/water");

		private static ResourceLocation prefix(String s) {
			return new ResourceLocation(AssortedDecor.MODID, s);
		}

		private static IOptionalNamedTag<Item> tag(String name) {
			return ItemTags.createOptional(prefix(name));
		}

		private static IOptionalNamedTag<Item> forgeTag(String name) {
			return ItemTags.createOptional(new ResourceLocation("forge", name));
		}
	}
}

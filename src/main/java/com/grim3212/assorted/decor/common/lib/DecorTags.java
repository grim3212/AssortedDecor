package com.grim3212.assorted.decor.common.lib;

import com.grim3212.assorted.decor.AssortedDecor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class DecorTags {

	public static class Blocks {

		public static final TagKey<Block> ROADWAYS = decorTag("roadways");
		public static final TagKey<Block> ROADWAYS_ALL = decorTag("roadways/all");
		public static final TagKey<Block> ROADWAYS_COLOR = decorTag("roadways/color");
		public static final TagKey<Block> FLURO = decorTag("fluro");

		public static final TagKey<Block> CONCRETE = forgeTag("concrete");
		public static final TagKey<Block> CONCRETE_POWDER = forgeTag("concrete_powder");
		public static final TagKey<Block> CARPET = forgeTag("carpet");

		private static TagKey<Block> forgeTag(String name) {
			return BlockTags.create(new ResourceLocation("forge", name));
		}

		private static TagKey<Block> decorTag(String name) {
			return BlockTags.create(new ResourceLocation(AssortedDecor.MODID, name));
		}
	}

	public static class Items {
		public static final TagKey<Item> BUCKETS_WATER = forgeTag("buckets/water");
		public static final TagKey<Item> INGOTS_ALUMINUM = forgeTag("ingots/aluminum");
		public static final TagKey<Item> INGOTS_STEEL = forgeTag("ingots/steel");

		public static final TagKey<Item> TAR = forgeTag("tar");
		public static final TagKey<Item> LANTERN_SOURCE = decorTag("lantern_source");
		public static final TagKey<Item> PAINT_ROLLERS = decorTag("paint_rollers");
		public static final TagKey<Item> ROADWAYS = decorTag("roadways");
		public static final TagKey<Item> ROADWAYS_ALL = decorTag("roadways/all");
		public static final TagKey<Item> ROADWAYS_COLOR = decorTag("roadways/color");
		public static final TagKey<Item> FLURO = decorTag("fluro");

		public static final TagKey<Item> CONCRETE = forgeTag("concrete");
		public static final TagKey<Item> CONCRETE_POWDER = forgeTag("concrete_powder");
		public static final TagKey<Item> CARPET = forgeTag("carpet");

		private static TagKey<Item> forgeTag(String name) {
			return ItemTags.create(new ResourceLocation("forge", name));
		}

		private static TagKey<Item> decorTag(String name) {
			return ItemTags.create(new ResourceLocation(AssortedDecor.MODID, name));
		}
	}
}

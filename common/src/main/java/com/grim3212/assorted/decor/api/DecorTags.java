package com.grim3212.assorted.decor.api;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class DecorTags {

    public static class Blocks {
        public static final TagKey<Block> BRUSH_DISALLOWED_BLOCKS = decorTag("brush_disallowed_blocks");
        public static final TagKey<Block> ROADWAYS = decorTag("roadways");
        public static final TagKey<Block> ROADWAYS_ALL = decorTag("roadways/all");
        public static final TagKey<Block> ROADWAYS_COLOR = decorTag("roadways/color");
        public static final TagKey<Block> FLURO = decorTag("fluro");
        public static final TagKey<Block> COLORIZER_ALWAYS_CUTOUT = decorTag("colorizer_always_cutout");

        private static TagKey<Block> decorTag(String name) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> LANTERN_SOURCE = decorTag("lantern_source");
        public static final TagKey<Item> PAINT_ROLLERS = decorTag("paint_rollers");
        public static final TagKey<Item> ROADWAYS = decorTag("roadways");
        public static final TagKey<Item> ROADWAYS_ALL = decorTag("roadways/all");
        public static final TagKey<Item> ROADWAYS_COLOR = decorTag("roadways/color");
        public static final TagKey<Item> FLURO = decorTag("fluro");
        public static final TagKey<Item> CAGE_SUPPORTED = decorTag("cage_supported");
        public static final TagKey<Item> INGOTS_ALUMINUM = commonTag("ingots/aluminum");
        public static final TagKey<Item> INGOTS_STEEL = commonTag("ingots/steel");
        public static final TagKey<Item> NUGGETS_ALUMINUM = commonTag("nuggets/aluminum");
        public static final TagKey<Item> NUGGETS_STEEL = commonTag("nuggets/steel");
        public static final TagKey<Item> TAR = commonTag("tar");

        private static TagKey<Item> decorTag(String name) {
            return TagKey.create(Registries.ITEM, new ResourceLocation(Constants.MOD_ID, name));
        }

        private static TagKey<Item> commonTag(String name) {
            return TagKey.create(Registries.ITEM, new ResourceLocation(Services.PLATFORM.getCommonTagPrefix(), name));
        }
    }
}

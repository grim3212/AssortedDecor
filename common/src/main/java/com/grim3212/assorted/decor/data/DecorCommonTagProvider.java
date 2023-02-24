package com.grim3212.assorted.decor.data;

import com.grim3212.assorted.decor.api.DecorTags;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.FluroBlock;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.lib.util.DyeHelper;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.NotImplementedException;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class DecorCommonTagProvider {

    public static class BlockTagProvider extends VanillaBlockTagsProvider {

        public BlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
            super(packOutput, lookup);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            throw new NotImplementedException();
        }

        @Override
        protected IntrinsicTagAppender<Block> tag(TagKey<Block> tag) {
            throw new NotImplementedException();
        }

        public void addCommonTags(Function<TagKey<Block>, IntrinsicTagAppender<Block>> tagger) {
            tagger.apply(DecorTags.Blocks.BRUSH_DISALLOWED_BLOCKS).add(Blocks.SPAWNER, Blocks.BEDROCK);

            tagger.apply(BlockTags.FENCES).add(DecorBlocks.COLORIZER_FENCE.get());
            tagger.apply(BlockTags.FENCE_GATES).add(DecorBlocks.COLORIZER_FENCE_GATE.get());
            tagger.apply(BlockTags.WALLS).add(DecorBlocks.COLORIZER_WALL.get());
            tagger.apply(BlockTags.TRAPDOORS).add(DecorBlocks.COLORIZER_TRAP_DOOR.get());
            tagger.apply(BlockTags.DOORS).add(DecorBlocks.COLORIZER_DOOR.get());
            tagger.apply(BlockTags.STAIRS).add(DecorBlocks.COLORIZER_STAIRS.get());
            tagger.apply(BlockTags.SLABS).add(DecorBlocks.COLORIZER_SLAB.get());

            tagger.apply(BlockTags.STANDING_SIGNS).add(DecorBlocks.NEON_SIGN.get());
            tagger.apply(BlockTags.WALL_SIGNS).add(DecorBlocks.NEON_SIGN_WALL.get());

            tagger.apply(BlockTags.DOORS).add(DecorBlocks.QUARTZ_DOOR.get(), DecorBlocks.GLASS_DOOR.get(), DecorBlocks.CHAIN_LINK_DOOR.get(), DecorBlocks.STEEL_DOOR.get());

            tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(DecorBlocks.QUARTZ_DOOR.get(), DecorBlocks.IRON_LANTERN.get(), DecorBlocks.ILLUMINATION_PLATE.get(), DecorBlocks.SIDEWALK.get(), DecorBlocks.ROADWAY.get(), DecorBlocks.ROADWAY_LIGHT.get(), DecorBlocks.ROADWAY_MANHOLE.get(), DecorBlocks.SIDING_HORIZONTAL.get(), DecorBlocks.SIDING_VERTICAL.get(), DecorBlocks.STEEL_DOOR.get(), DecorBlocks.STONE_PATH.get(), DecorBlocks.DECORATIVE_STONE.get());
            tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(DecorBlocks.colorizerBlocks());

            tagger.apply(DecorTags.Blocks.ROADWAYS).add(DecorBlocks.ROADWAY.get());
            DecorBlocks.ROADWAY_COLORS.forEach((color, roadway) -> {
                tagger.apply(DecorTags.Blocks.ROADWAYS_COLOR).add(roadway.get());
                tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(roadway.get());
            });

            tagger.apply(DecorTags.Blocks.ROADWAYS).addTag(DecorTags.Blocks.ROADWAYS_COLOR);
            tagger.apply(DecorTags.Blocks.ROADWAYS_ALL).addTag(DecorTags.Blocks.ROADWAYS);
            tagger.apply(DecorTags.Blocks.ROADWAYS_ALL).add(DecorBlocks.ROADWAY_LIGHT.get());
            tagger.apply(DecorTags.Blocks.ROADWAYS_ALL).add(DecorBlocks.ROADWAY_MANHOLE.get());

            FluroBlock.FLURO_BY_DYE.entrySet().stream().forEach((x) -> tagger.apply(DecorTags.Blocks.FLURO).add(x.getValue().get()));
        }
    }

    public static class ItemTagProvider extends VanillaItemTagsProvider {

        public ItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, TagsProvider<Block> blockTags) {
            super(output, lookup, blockTags);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            throw new NotImplementedException();
        }

        @Override
        protected IntrinsicTagAppender<Item> tag(TagKey<Item> tag) {
            throw new NotImplementedException();
        }

        @Override
        protected void copy(TagKey<Block> blockTag, TagKey<Item> itemTag) {
            throw new NotImplementedException();
        }

        public void addCommonTags(Function<TagKey<Item>, IntrinsicTagAppender<Item>> tagger, Consumer<Tuple<TagKey<Block>, TagKey<Item>>> copier) {
            tagger.apply(DecorTags.Items.TAR).add(DecorItems.TARBALL.get());
            tagger.apply(LibCommonTags.Items.FENCES).add(DecorBlocks.COLORIZER_FENCE.get().asItem());
            tagger.apply(LibCommonTags.Items.FENCE_GATES).add(DecorBlocks.COLORIZER_FENCE_GATE.get().asItem());
            tagger.apply(ItemTags.WALLS).add(DecorBlocks.COLORIZER_WALL.get().asItem());
            tagger.apply(ItemTags.TRAPDOORS).add(DecorBlocks.COLORIZER_TRAP_DOOR.get().asItem());
            tagger.apply(ItemTags.STAIRS).add(DecorBlocks.COLORIZER_STAIRS.get().asItem());
            tagger.apply(ItemTags.SLABS).add(DecorBlocks.COLORIZER_SLAB.get().asItem());

            tagger.apply(ItemTags.SIGNS).add(DecorItems.NEON_SIGN.get());

            tagger.apply(DecorTags.Items.LANTERN_SOURCE).addOptionalTag(ItemTags.CANDLES.location());
            tagger.apply(DecorTags.Items.LANTERN_SOURCE).add(DecorBlocks.ILLUMINATION_TUBE.get().asItem(), Blocks.TORCH.asItem(), Blocks.SOUL_TORCH.asItem());

            DecorItems.PAINT_ROLLER_COLORS.forEach((color, roller) -> {
                tagger.apply(DecorTags.Items.PAINT_ROLLERS).add(roller.get());
                tagger.apply(LibCommonTags.Items.DYES).add(roller.get());
                tagger.apply(DyeHelper.getDyeTag(color)).add(roller.get());
            });

            copier.accept(new Tuple<>(DecorTags.Blocks.ROADWAYS, DecorTags.Items.ROADWAYS));
            copier.accept(new Tuple<>(DecorTags.Blocks.ROADWAYS_ALL, DecorTags.Items.ROADWAYS_ALL));
            copier.accept(new Tuple<>(DecorTags.Blocks.ROADWAYS_COLOR, DecorTags.Items.ROADWAYS_COLOR));
            copier.accept(new Tuple<>(DecorTags.Blocks.FLURO, DecorTags.Items.FLURO));
        }
    }
}

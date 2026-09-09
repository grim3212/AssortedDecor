package com.grim3212.assorted.decor.data;

import com.grim3212.assorted.decor.api.DecorTags;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.lib.data.LibItemTagProvider;
import com.grim3212.assorted.lib.util.DyeHelper;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DecorItemTagProvider extends LibItemTagProvider {


    public DecorItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, TagAppender<Item>> appender, BiConsumer<TagKey<Block>, TagKey<Item>> copier) {
        // See DecorBlockTagProvider: TagAppender only accepts ResourceKeys now.
        Function<TagKey<Item>, ItemTagger> tagger = (tag) -> new ItemTagger(appender.apply(tag));

        tagger.apply(DecorTags.Items.TAR).add(DecorItems.TARBALL.get());
        tagger.apply(LibCommonTags.Items.FENCES).add(DecorBlocks.COLORIZER_FENCE.get().asItem());
        tagger.apply(LibCommonTags.Items.FENCE_GATES).add(DecorBlocks.COLORIZER_FENCE_GATE.get().asItem());
        tagger.apply(BlockItemTags.WALLS.item()).add(DecorBlocks.COLORIZER_WALL.get().asItem());
        tagger.apply(BlockItemTags.TRAPDOORS.item()).add(DecorBlocks.COLORIZER_TRAP_DOOR.get().asItem());
        tagger.apply(BlockItemTags.STAIRS.item()).add(DecorBlocks.COLORIZER_STAIRS.get().asItem());
        tagger.apply(BlockItemTags.SLABS.item()).add(DecorBlocks.COLORIZER_SLAB.get().asItem());

        tagger.apply(ItemTags.SIGNS).add(DecorItems.NEON_SIGN.get());

        tagger.apply(DecorTags.Items.LANTERN_SOURCE).addOptionalTag(ItemTags.CANDLES);
        tagger.apply(DecorTags.Items.LANTERN_SOURCE).add(DecorBlocks.ILLUMINATION_TUBE.get().asItem(), Blocks.TORCH.asItem(), Blocks.SOUL_TORCH.asItem());

        DecorItems.PAINT_ROLLER_COLORS.forEach((color, roller) -> {
            tagger.apply(DecorTags.Items.PAINT_ROLLERS).add(roller.get());
            tagger.apply(LibCommonTags.Items.DYES).add(roller.get());
            tagger.apply(DyeHelper.getDyeTag(color)).add(roller.get());
        });

        copier.accept(DecorTags.Blocks.ROADWAYS, DecorTags.Items.ROADWAYS);
        copier.accept(DecorTags.Blocks.ROADWAYS_ALL, DecorTags.Items.ROADWAYS_ALL);
        copier.accept(DecorTags.Blocks.ROADWAYS_COLOR, DecorTags.Items.ROADWAYS_COLOR);
        copier.accept(DecorTags.Blocks.FLURO, DecorTags.Items.FLURO);
    }

    private record ItemTagger(TagAppender<Item> appender) {

        ItemTagger add(Item... items) {
            for (Item item : items) {
                this.appender.add(BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow());
            }

            return this;
        }

        ItemTagger addTag(TagKey<Item> tag) {
            this.appender.addTag(tag);
            return this;
        }

        ItemTagger addOptionalTag(TagKey<Item> tag) {
            this.appender.addOptionalTag(tag);
            return this;
        }
    }
}

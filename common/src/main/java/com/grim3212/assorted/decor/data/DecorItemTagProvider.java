package com.grim3212.assorted.decor.data;

import com.grim3212.assorted.decor.api.DecorTags;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.lib.data.LibItemTagProvider;
import com.grim3212.assorted.lib.util.DyeHelper;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DecorItemTagProvider extends LibItemTagProvider {


    public DecorItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, TagsProvider<Block> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, IntrinsicTagAppender<Item>> tagger, BiConsumer<TagKey<Block>, TagKey<Item>> copier) {
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

        copier.accept(DecorTags.Blocks.ROADWAYS, DecorTags.Items.ROADWAYS);
        copier.accept(DecorTags.Blocks.ROADWAYS_ALL, DecorTags.Items.ROADWAYS_ALL);
        copier.accept(DecorTags.Blocks.ROADWAYS_COLOR, DecorTags.Items.ROADWAYS_COLOR);
        copier.accept(DecorTags.Blocks.FLURO, DecorTags.Items.FLURO);
    }
}

package com.grim3212.assorted.decor.data;

import com.grim3212.assorted.decor.api.DecorTags;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.FluroBlock;
import com.grim3212.assorted.lib.data.LibBlockTagProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class DecorBlockTagProvider extends LibBlockTagProvider {

    public DecorBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOutput, lookup);
    }

    @Override
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
        tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(DecorBlocks.colorizerBlocks().stream().map(IRegistryObject::get).toArray(Block[]::new));

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

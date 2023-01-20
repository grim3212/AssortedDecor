package com.grim3212.assorted.decor.common.data;

import java.util.concurrent.CompletableFuture;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.block.FluroBlock;
import com.grim3212.assorted.decor.common.item.PaintRollerItem;
import com.grim3212.assorted.decor.common.util.DecorTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DecorBlockTagProvider extends BlockTagsProvider {

	public DecorBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, AssortedDecor.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		this.tag(BlockTags.FENCES).add(DecorBlocks.COLORIZER_FENCE.get());
		this.tag(BlockTags.FENCE_GATES).add(DecorBlocks.COLORIZER_FENCE_GATE.get());
		this.tag(BlockTags.WALLS).add(DecorBlocks.COLORIZER_WALL.get());
		this.tag(BlockTags.TRAPDOORS).add(DecorBlocks.COLORIZER_TRAP_DOOR.get());
		this.tag(BlockTags.DOORS).add(DecorBlocks.COLORIZER_DOOR.get());
		this.tag(BlockTags.STAIRS).add(DecorBlocks.COLORIZER_STAIRS.get());
		this.tag(BlockTags.SLABS).add(DecorBlocks.COLORIZER_SLAB.get());

		this.tag(BlockTags.STANDING_SIGNS).add(DecorBlocks.NEON_SIGN.get());
		this.tag(BlockTags.WALL_SIGNS).add(DecorBlocks.NEON_SIGN_WALL.get());

		this.tag(BlockTags.DOORS).add(DecorBlocks.QUARTZ_DOOR.get(), DecorBlocks.GLASS_DOOR.get(), DecorBlocks.CHAIN_LINK_DOOR.get(), DecorBlocks.STEEL_DOOR.get());

		this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(DecorBlocks.QUARTZ_DOOR.get(), DecorBlocks.IRON_LANTERN.get(), DecorBlocks.ILLUMINATION_PLATE.get(), DecorBlocks.SIDEWALK.get(), DecorBlocks.ROADWAY.get(), DecorBlocks.ROADWAY_LIGHT.get(), DecorBlocks.ROADWAY_MANHOLE.get(), DecorBlocks.SIDING_HORIZONTAL.get(), DecorBlocks.SIDING_VERTICAL.get(), DecorBlocks.STEEL_DOOR.get(), DecorBlocks.STONE_PATH.get(), DecorBlocks.DECORATIVE_STONE.get());
		this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(DecorBlocks.colorizerBlocks());

		this.tag(DecorTags.Blocks.ROADWAYS).add(DecorBlocks.ROADWAY.get());
		DecorBlocks.ROADWAY_COLORS.forEach((color, roadway) -> {
			this.tag(DecorTags.Blocks.ROADWAYS_COLOR).add(roadway.get());
			this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(roadway.get());
		});

		this.tag(DecorTags.Blocks.ROADWAYS).addTag(DecorTags.Blocks.ROADWAYS_COLOR);
		this.tag(DecorTags.Blocks.ROADWAYS_ALL).addTag(DecorTags.Blocks.ROADWAYS);
		this.tag(DecorTags.Blocks.ROADWAYS_ALL).add(DecorBlocks.ROADWAY_LIGHT.get());
		this.tag(DecorTags.Blocks.ROADWAYS_ALL).add(DecorBlocks.ROADWAY_MANHOLE.get());

		FluroBlock.FLURO_BY_DYE.entrySet().stream().forEach((x) -> this.tag(DecorTags.Blocks.FLURO).add(x.getValue().get()));
		PaintRollerItem.CONCRETE_BY_DYE.entrySet().stream().forEach((x) -> this.tag(DecorTags.Blocks.CONCRETE).add(x.getValue()));
		PaintRollerItem.CONCRETE_POWDER_BY_DYE.entrySet().stream().forEach((x) -> this.tag(DecorTags.Blocks.CONCRETE_POWDER).add(x.getValue()));
		PaintRollerItem.CARPET_BY_DYE.entrySet().stream().forEach((x) -> this.tag(DecorTags.Blocks.CARPET).add(x.getValue()));
	}

	@Override
	public String getName() {
		return "Assorted Decor block tags";
	}
}

package com.grim3212.assorted.decor.common.data;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.DecorBlocks;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DecorBlockTagProvider extends BlockTagsProvider {

	public DecorBlockTagProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
		super(generatorIn, AssortedDecor.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		this.tag(BlockTags.FENCES).add(DecorBlocks.COLORIZER_FENCE.get());
		this.tag(BlockTags.FENCE_GATES).add(DecorBlocks.COLORIZER_FENCE_GATE.get());
		this.tag(BlockTags.WALLS).add(DecorBlocks.COLORIZER_WALL.get());
		this.tag(BlockTags.TRAPDOORS).add(DecorBlocks.COLORIZER_TRAP_DOOR.get());
		this.tag(BlockTags.DOORS).add(DecorBlocks.COLORIZER_DOOR.get());
		this.tag(BlockTags.STAIRS).add(DecorBlocks.COLORIZER_STAIRS.get());
		this.tag(BlockTags.SLABS).add(DecorBlocks.COLORIZER_SLAB.get());

		this.tag(BlockTags.STANDING_SIGNS).add(DecorBlocks.NEON_SIGN.get());
		this.tag(BlockTags.WALL_SIGNS).add(DecorBlocks.NEON_SIGN_WALL.get());

		this.tag(BlockTags.DOORS).add(DecorBlocks.QUARTZ_DOOR.get());

		this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(DecorBlocks.QUARTZ_DOOR.get());
		this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(DecorBlocks.colorizerBlocks());
	}

	@Override
	public String getName() {
		return "Assorted Decor block tags";
	}
}

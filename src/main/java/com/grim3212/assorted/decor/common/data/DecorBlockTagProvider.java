package com.grim3212.assorted.decor.common.data;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.DecorBlocks;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DecorBlockTagProvider extends BlockTagsProvider {

	public DecorBlockTagProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
		super(generatorIn, AssortedDecor.MODID, existingFileHelper);
	}

	@Override
	protected void registerTags() {
		this.getOrCreateBuilder(BlockTags.FENCES).addItemEntry(DecorBlocks.COLORIZER_FENCE.get());
		this.getOrCreateBuilder(BlockTags.FENCE_GATES).addItemEntry(DecorBlocks.COLORIZER_FENCE_GATE.get());
		this.getOrCreateBuilder(BlockTags.WALLS).addItemEntry(DecorBlocks.COLORIZER_WALL.get());
		this.getOrCreateBuilder(BlockTags.TRAPDOORS).addItemEntry(DecorBlocks.COLORIZER_TRAP_DOOR.get());
		this.getOrCreateBuilder(BlockTags.DOORS).addItemEntry(DecorBlocks.COLORIZER_DOOR.get());
	}
}

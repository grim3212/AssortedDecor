package com.grim3212.assorted.decor.common.data;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.item.DecorItems;
import com.grim3212.assorted.decor.common.lib.DecorTags;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DecorItemTagProvider extends ItemTagsProvider {

	public DecorItemTagProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, blockTagProvider, AssortedDecor.MODID, existingFileHelper);
	}

	@Override
	protected void registerTags() {
		this.getOrCreateBuilder(DecorTags.Items.BUCKETS_WATER).add(Items.WATER_BUCKET);
		this.getOrCreateBuilder(Tags.Items.FENCES).addItemEntry(DecorBlocks.COLORIZER_FENCE.get().asItem());
		this.getOrCreateBuilder(Tags.Items.FENCE_GATES).addItemEntry(DecorBlocks.COLORIZER_FENCE_GATE.get().asItem());
		this.getOrCreateBuilder(ItemTags.WALLS).addItemEntry(DecorBlocks.COLORIZER_WALL.get().asItem());
		this.getOrCreateBuilder(ItemTags.TRAPDOORS).addItemEntry(DecorBlocks.COLORIZER_TRAP_DOOR.get().asItem());
		this.getOrCreateBuilder(ItemTags.DOORS).addItemEntry(DecorBlocks.COLORIZER_DOOR.get().asItem());
		this.getOrCreateBuilder(ItemTags.STAIRS).addItemEntry(DecorBlocks.COLORIZER_STAIRS.get().asItem());
		this.getOrCreateBuilder(ItemTags.SLABS).addItemEntry(DecorBlocks.COLORIZER_SLAB.get().asItem());

		this.getOrCreateBuilder(ItemTags.SIGNS).addItemEntry(DecorItems.NEON_SIGN.get());

		this.getOrCreateBuilder(ItemTags.DOORS).addItemEntry(DecorBlocks.QUARTZ_DOOR.get().asItem());
	}

	@Override
	public String getName() {
		return "Assorted Decor item tags";
	}
}

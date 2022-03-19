package com.grim3212.assorted.decor.common.data;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.item.DecorItems;
import com.grim3212.assorted.decor.common.lib.DecorTags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DecorItemTagProvider extends ItemTagsProvider {

	public DecorItemTagProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, blockTagProvider, AssortedDecor.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		this.tag(DecorTags.Items.BUCKETS_WATER).add(Items.WATER_BUCKET);
		this.tag(Tags.Items.FENCES).add(DecorBlocks.COLORIZER_FENCE.get().asItem());
		this.tag(Tags.Items.FENCE_GATES).add(DecorBlocks.COLORIZER_FENCE_GATE.get().asItem());
		this.tag(ItemTags.WALLS).add(DecorBlocks.COLORIZER_WALL.get().asItem());
		this.tag(ItemTags.TRAPDOORS).add(DecorBlocks.COLORIZER_TRAP_DOOR.get().asItem());
		this.tag(ItemTags.DOORS).add(DecorBlocks.COLORIZER_DOOR.get().asItem());
		this.tag(ItemTags.STAIRS).add(DecorBlocks.COLORIZER_STAIRS.get().asItem());
		this.tag(ItemTags.SLABS).add(DecorBlocks.COLORIZER_SLAB.get().asItem());

		this.tag(ItemTags.SIGNS).add(DecorItems.NEON_SIGN.get());

		this.tag(ItemTags.DOORS).add(DecorBlocks.QUARTZ_DOOR.get().asItem());

		this.tag(DecorTags.Items.LANTERN_SOURCE).addTag(ItemTags.CANDLES);
		this.tag(DecorTags.Items.LANTERN_SOURCE).add(DecorBlocks.ILLUMINATION_TUBE.get().asItem(), Blocks.TORCH.asItem(), Blocks.SOUL_TORCH.asItem());

		this.tag(DecorTags.Items.TAR).add(DecorItems.TARBALL.get());

		DecorItems.PAINT_ROLLER_COLORS.forEach((color, roller) -> {
			this.tag(DecorTags.Items.PAINT_ROLLERS).add(roller.get());
			this.tag(Tags.Items.DYES).add(roller.get());
			this.tag(color.getTag()).add(roller.get());
		});

		this.copy(DecorTags.Blocks.ROADWAYS, DecorTags.Items.ROADWAYS);
		this.copy(DecorTags.Blocks.ROADWAYS_ALL, DecorTags.Items.ROADWAYS_ALL);
		this.copy(DecorTags.Blocks.ROADWAYS_COLOR, DecorTags.Items.ROADWAYS_COLOR);
		this.copy(DecorTags.Blocks.FLURO, DecorTags.Items.FLURO);
		this.copy(DecorTags.Blocks.CONCRETE, DecorTags.Items.CONCRETE);
		this.copy(DecorTags.Blocks.CONCRETE_POWDER, DecorTags.Items.CONCRETE_POWDER);
		this.copy(DecorTags.Blocks.CARPET, DecorTags.Items.CARPET);
	}

	@Override
	public String getName() {
		return "Assorted Decor item tags";
	}
}

package com.grim3212.assorted.decor.common.blocks;

import com.grim3212.assorted.decor.api.colorizer.ICanColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RoadwayBlock extends Block implements ICanColor {

	public RoadwayBlock(Properties props) {
		super(props);
	}

	@Override
	public DyeColor currentColor(BlockState state) {
		return null;
	}

	@Override
	public BlockState stateForColor(BlockState state, DyeColor color) {
		return DecorBlocks.ROADWAY_COLORS.get(color).get().defaultBlockState();
	}

}

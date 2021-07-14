package com.grim3212.assorted.decor.common.block.colorizer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class ColorizerCounterBlock extends ColorizerSideBlock {

	public static final VoxelShape COUNTER_FLOOR = Block.box(0.0F, 16.0F, 0.0F, 16.0F, 12.96F, 16.0F);
	public static final VoxelShape COUNTER_CEILING = Block.box(0.0F, 0.0F, 0.0F, 16.0F, 3.04F, 16.0F);
	public static final VoxelShape COUNTER_NORTH = Block.box(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 3.04F);
	public static final VoxelShape COUNTER_SOUTH = Block.box(0.0F, 0.0F, 16.0F, 16.0F, 16.0F, 12.96F);
	public static final VoxelShape COUNTER_WEST = Block.box(0.0F, 0.0F, 0.0F, 3.04F, 16.0F, 16.0F);
	public static final VoxelShape COUNTER_EAST = Block.box(16.0F, 0.0F, 0.0F, 12.96F, 16.0F, 16.0F);

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch(getFacing(state)) {
		case DOWN:
			return COUNTER_CEILING;
		case EAST:
			return COUNTER_EAST;
		case NORTH:
			return COUNTER_NORTH;
		case SOUTH:
			return COUNTER_SOUTH;
		case WEST:
			return COUNTER_WEST;
		case UP:
		default:
			return COUNTER_FLOOR;
		}
	}

}

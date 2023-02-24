package com.grim3212.assorted.decor.common.blocks.colorizer;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

public class ColorizerChairBlock extends ColorizerRotateBlock {

	private static final VoxelShape BASE = Block.box(0, 0, 0, 16, 8, 16);
	private static final VoxelShape EAST = Block.box(12.96F, 0.0F, 0.0F, 16F, 16F, 16F);
	private static final VoxelShape NORTH = Block.box(0.0F, 0.0F, 0.0F, 16F, 16F, 3.04F);
	private static final VoxelShape WEST = Block.box(0.0F, 0.0F, 0.0F, 3.04F, 16F, 16F);
	private static final VoxelShape SOUTH = Block.box(0.0F, 0.0F, 12.96F, 16F, 16F, 16F);

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		switch (state.getValue(FACING)) {
		case EAST:
			return Shapes.or(BASE, EAST);
		case NORTH:
			return Shapes.or(BASE, NORTH);
		case SOUTH:
			return Shapes.or(BASE, SOUTH);
		case WEST:
			return Shapes.or(BASE, WEST);
		default:
			break;
		}

		return Shapes.block();
	}

}

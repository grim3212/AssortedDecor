package com.grim3212.assorted.decor.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IlluminationPlateBlock extends IlluminationTubeBlock {

	private static final VoxelShape UP_AABB = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 2.0D, 12.0D);
	private static final VoxelShape DOWN_AABB = Block.box(4.0D, 14.0D, 4.0D, 12.0D, 16.0D, 12.0D);
	private static final VoxelShape NORTH_AABB = Block.box(4.0D, 4.0D, 14.0D, 12.0D, 12.0D, 16.0D);
	private static final VoxelShape SOUTH_AABB = Block.box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 2.0D);
	private static final VoxelShape WEST_AABB = Block.box(14.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D);
	private static final VoxelShape EAST_AABB = Block.box(0.0D, 4.0D, 4.0D, 2.0D, 12.0D, 12.0D);

	public IlluminationPlateBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		switch (state.getValue(FACING)) {
			case EAST:
				return EAST_AABB;
			case NORTH:
				return NORTH_AABB;
			case SOUTH:
				return SOUTH_AABB;
			case UP:
				return UP_AABB;
			case WEST:
				return WEST_AABB;
			case DOWN:
			default:
				return DOWN_AABB;
		}
	}
}

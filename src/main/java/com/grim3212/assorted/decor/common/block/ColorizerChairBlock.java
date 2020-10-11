package com.grim3212.assorted.decor.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class ColorizerChairBlock extends ColorizerRotateBlock {

	private static final VoxelShape BASE = Block.makeCuboidShape(0, 0, 0, 16, 8, 16);
	private static final VoxelShape EAST = Block.makeCuboidShape(12.96F, 0.0F, 0.0F, 16F, 16F, 16F);
	private static final VoxelShape NORTH = Block.makeCuboidShape(0.0F, 0.0F, 0.0F, 16F, 16F, 3.04F);
	private static final VoxelShape WEST = Block.makeCuboidShape(0.0F, 0.0F, 0.0F, 3.04F, 16F, 16F);
	private static final VoxelShape SOUTH = Block.makeCuboidShape(0.0F, 0.0F, 12.96F, 16F, 16F, 16F);

	public ColorizerChairBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch (state.get(FACING)) {
		case EAST:
			return VoxelShapes.or(BASE, EAST);
		case NORTH:
			return VoxelShapes.or(BASE, NORTH);
		case SOUTH:
			return VoxelShapes.or(BASE, SOUTH);
		case WEST:
			return VoxelShapes.or(BASE, WEST);
		default:
			break;
		}

		return VoxelShapes.fullCube();
	}

}

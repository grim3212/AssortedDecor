package com.grim3212.assorted.decor.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class ColorizerFireringBlock extends ColorizerFireplaceBaseBlock {

	protected static final VoxelShape FIRERING = Block.makeCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 6.4F, 16.0F);

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return FIRERING;
	}
}

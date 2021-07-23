package com.grim3212.assorted.decor.common.block.colorizer;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

public class ColorizerFireringBlock extends ColorizerFireplaceBaseBlock {

	protected static final VoxelShape FIRERING = Block.box(0.0F, 0.0F, 0.0F, 16.0F, 6.4F, 16.0F);

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return FIRERING;
	}
}

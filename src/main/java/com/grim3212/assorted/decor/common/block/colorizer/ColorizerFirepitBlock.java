package com.grim3212.assorted.decor.common.block.colorizer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class ColorizerFirepitBlock extends ColorizerFireplaceBaseBlock {

	protected static final VoxelShape FIREPIT = Block.box(0.0F, 0.0F, 0.0F, 16.0F, 9.6F, 16.0F);

	private final boolean covered;

	public ColorizerFirepitBlock(boolean covered) {
		super();
		this.covered = covered;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return this.covered ? VoxelShapes.block() : FIREPIT;
	}
}

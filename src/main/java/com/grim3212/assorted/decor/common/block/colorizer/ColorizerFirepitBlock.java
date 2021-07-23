package com.grim3212.assorted.decor.common.block.colorizer;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

public class ColorizerFirepitBlock extends ColorizerFireplaceBaseBlock {

	protected static final VoxelShape FIREPIT = Block.box(0.0F, 0.0F, 0.0F, 16.0F, 9.6F, 16.0F);

	private final boolean covered;

	public ColorizerFirepitBlock(boolean covered) {
		super();
		this.covered = covered;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return this.covered ? Shapes.block() : FIREPIT;
	}
}

package com.grim3212.assorted.decor.common.block.colorizer;

import com.grim3212.assorted.decor.common.util.ColorizerUtil;
import com.grim3212.assorted.decor.common.util.ColorizerUtil.SlopeType;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

public class ColorizerSlopeSideBlock extends ColorizerSideBlock {

	private final SlopeType type;

	public ColorizerSlopeSideBlock(SlopeType type) {
		this.type = type;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return ColorizerUtil.addAxisAlignedBoxes(state, worldIn, pos, context, this.type.getNumPieces());
	}

}

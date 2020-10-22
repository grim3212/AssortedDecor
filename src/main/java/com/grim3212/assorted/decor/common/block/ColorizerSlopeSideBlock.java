package com.grim3212.assorted.decor.common.block;

import com.grim3212.assorted.decor.common.util.ColorizerUtil;
import com.grim3212.assorted.decor.common.util.ColorizerUtil.SlopeType;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class ColorizerSlopeSideBlock extends ColorizerSideBlock {

	private final SlopeType type;

	public ColorizerSlopeSideBlock(SlopeType type) {
		this.type = type;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return ColorizerUtil.addAxisAlignedBoxes(state, worldIn, pos, context, this.type.getNumPieces());
	}

}

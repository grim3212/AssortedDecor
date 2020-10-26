package com.grim3212.assorted.decor.common.block.colorizer;

import net.minecraft.block.BlockState;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

public abstract class ColorizerHorizontalBlock extends ColorizerBlock {

	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(HORIZONTAL_FACING)));
	}

}

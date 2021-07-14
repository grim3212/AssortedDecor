package com.grim3212.assorted.decor.common.block.colorizer;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class ColorizerSideBlock extends ColorizerBlock implements IWaterLoggable {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

	public ColorizerSideBlock() {
		this.registerDefaultState(this.stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL).setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACE, HORIZONTAL_FACING, WATERLOGGED);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(HORIZONTAL_FACING, rot.rotate(state.getValue(HORIZONTAL_FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(HORIZONTAL_FACING)));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return isSideSolidForDirection(worldIn, pos, getFacing(state).getOpposite());
	}

	public static boolean isSideSolidForDirection(IWorldReader reader, BlockPos pos, Direction direction) {
		BlockPos blockpos = pos.relative(direction);
		return reader.getBlockState(blockpos).isFaceSturdy(reader, blockpos, direction.getOpposite());
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		for (Direction direction : context.getNearestLookingDirections()) {
			BlockState blockstate;
			if (direction.getAxis() == Direction.Axis.Y) {
				blockstate = this.defaultBlockState().setValue(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).setValue(HORIZONTAL_FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
			} else {
				blockstate = this.defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(HORIZONTAL_FACING, direction.getOpposite()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
			}

			if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
				return blockstate;
			}
		}

		return null;
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.getValue(WATERLOGGED)) {
			worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}

		return getFacing(stateIn).getOpposite() == facing && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	protected static Direction getFacing(BlockState state) {
		switch ((AttachFace) state.getValue(FACE)) {
		case CEILING:
			return Direction.DOWN;
		case FLOOR:
			return Direction.UP;
		default:
			return state.getValue(HORIZONTAL_FACING);
		}
	}

}

package com.grim3212.assorted.decor.common.block;

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
	public static final EnumProperty<AttachFace> FACE = BlockStateProperties.FACE;
	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

	public ColorizerSideBlock(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(FACE, AttachFace.WALL).with(WATERLOGGED, false));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACE, HORIZONTAL_FACING, WATERLOGGED);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(HORIZONTAL_FACING)));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return isSideSolidForDirection(worldIn, pos, getFacing(state).getOpposite());
	}

	public static boolean isSideSolidForDirection(IWorldReader reader, BlockPos pos, Direction direction) {
		BlockPos blockpos = pos.offset(direction);
		return reader.getBlockState(blockpos).isSolidSide(reader, blockpos, direction.getOpposite());
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
		for (Direction direction : context.getNearestLookingDirections()) {
			BlockState blockstate;
			if (direction.getAxis() == Direction.Axis.Y) {
				blockstate = this.getDefaultState().with(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing()).with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
			} else {
				blockstate = this.getDefaultState().with(FACE, AttachFace.WALL).with(HORIZONTAL_FACING, direction.getOpposite()).with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
			}

			if (blockstate.isValidPosition(context.getWorld(), context.getPos())) {
				return blockstate;
			}
		}

		return null;
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		
		return getFacing(stateIn).getOpposite() == facing && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	protected static Direction getFacing(BlockState state) {
		switch ((AttachFace) state.get(FACE)) {
		case CEILING:
			return Direction.DOWN;
		case FLOOR:
			return Direction.UP;
		default:
			return state.get(HORIZONTAL_FACING);
		}
	}

}

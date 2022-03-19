package com.grim3212.assorted.decor.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IlluminationTubeBlock extends Block implements SimpleWaterloggedBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	protected static final VoxelShape STANDING = Block.box(6.4D, 0.0D, 6.4D, 9.6D, 9.6D, 9.6D);
	protected static final VoxelShape STANDING_FLIPPED = Block.box(6.4D, 6.28D, 6.4D, 9.6D, 16.0D, 9.6D);
	protected static final VoxelShape TORCH_NORTH = Block.box(5.59998D, 3.2D, 11.199D, 10.39984D, 12.8D, 16.0D);
	protected static final VoxelShape TORCH_SOUTH = Block.box(5.59998D, 3.2D, 0.0D, 10.39984D, 12.8D, 4.8D);
	protected static final VoxelShape TORCH_WEST = Block.box(11.199D, 3.2D, 5.59998D, 16.0D, 12.8D, 10.39984D);
	protected static final VoxelShape TORCH_EAST = Block.box(0.0D, 3.2D, 5.59998D, 4.8D, 12.8D, 10.39984D);

	public IlluminationTubeBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(WATERLOGGED, false));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.getValue(WATERLOGGED)) {
			worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}

		return facing.getOpposite() == stateIn.getValue(FACING) && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : stateIn;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		switch (state.getValue(FACING)) {
			case EAST:
				return TORCH_EAST;
			case NORTH:
				return TORCH_NORTH;
			case SOUTH:
				return TORCH_SOUTH;
			case UP:
				return STANDING;
			case WEST:
				return TORCH_WEST;
			case DOWN:
			default:
				return STANDING_FLIPPED;
		}
	}

	@Override
	public VoxelShape getBlockSupportShape(BlockState state, BlockGetter reader, BlockPos pos) {
		return Shapes.empty();
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
		Direction direction = state.getValue(FACING);
		BlockPos blockpos = pos.relative(direction.getOpposite());
		BlockState blockstate = worldIn.getBlockState(blockpos);
		return direction == Direction.UP || direction == Direction.DOWN ? canSupportCenter(worldIn, blockpos, direction) : blockstate.isFaceSturdy(worldIn, blockpos, direction);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockstate = this.defaultBlockState();
		LevelReader iworldreader = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		Direction[] adirection = context.getNearestLookingDirections();
		FluidState fluidstate = context.getLevel().getFluidState(blockpos);

		for (Direction direction : adirection) {
			Direction direction1 = direction.getOpposite();
			blockstate = blockstate.setValue(FACING, direction1);
			if (blockstate.canSurvive(iworldreader, blockpos)) {
				return blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
			}
		}

		return null;
	}

	@Override
	public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation direction) {
		return super.rotate(state, world, pos, direction);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}
}

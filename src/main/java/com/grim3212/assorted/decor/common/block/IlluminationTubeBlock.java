package com.grim3212.assorted.decor.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class IlluminationTubeBlock extends Block {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	protected static final VoxelShape STANDING = Block.box(6.4D, 0.0D, 6.4D, 9.6D, 9.6D, 9.6D);
	protected static final VoxelShape STANDING_FLIPPED = Block.box(6.4D, 6.28D, 6.4D, 9.6D, 16.0D, 9.6D);
	protected static final VoxelShape TORCH_NORTH = Block.box(5.59998D, 3.2D, 11.199D, 10.39984D, 12.8D, 16.0D);
	protected static final VoxelShape TORCH_SOUTH = Block.box(5.59998D, 3.2D, 0.0D, 10.39984D, 12.8D, 4.8D);
	protected static final VoxelShape TORCH_WEST = Block.box(11.199D, 3.2D, 5.59998D, 16.0D, 12.8D, 10.39984D);
	protected static final VoxelShape TORCH_EAST = Block.box(0.0D, 3.2D, 5.59998D, 4.8D, 12.8D, 10.39984D);

	public IlluminationTubeBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
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
	public VoxelShape getBlockSupportShape(BlockState state, IBlockReader reader, BlockPos pos) {
		return VoxelShapes.empty();
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
		Direction direction = state.getValue(FACING);
		BlockPos blockpos = pos.relative(direction.getOpposite());
		BlockState blockstate = worldIn.getBlockState(blockpos);
		return direction == Direction.UP || direction == Direction.DOWN ? canSupportCenter(worldIn, blockpos, direction) : blockstate.isFaceSturdy(worldIn, blockpos, direction);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState blockstate = this.defaultBlockState();
		IWorldReader iworldreader = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		Direction[] adirection = context.getNearestLookingDirections();

		for (Direction direction : adirection) {
			Direction direction1 = direction.getOpposite();
			blockstate = blockstate.setValue(FACING, direction1);
			if (blockstate.canSurvive(iworldreader, blockpos)) {
				return blockstate;
			}
		}

		return null;
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		return facing.getOpposite() == stateIn.getValue(FACING) && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : stateIn;
	}

	@Override
	public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
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

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}

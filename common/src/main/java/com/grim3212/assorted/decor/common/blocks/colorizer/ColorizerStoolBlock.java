package com.grim3212.assorted.decor.common.blocks.colorizer;

import com.grim3212.assorted.decor.common.blocks.DecorBlocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;

public class ColorizerStoolBlock extends ColorizerSideBlock {

	public static final BooleanProperty UP = BooleanProperty.create("up");

	public static final VoxelShape POT_STOOL = Block.box(2.88F, 0.0F, 2.88F, 13.12F, 16F, 13.12F);
	private static final VoxelShape STOOL_FLOOR = Block.box(2.88F, 0.0F, 2.88F, 13.12F, 10.08F, 13.12F);
	private static final VoxelShape STOOL_CEILING = Block.box(2.88F, 5.92F, 2.88F, 13.12F, 16F, 13.12F);
	private static final VoxelShape STOOL_NORTH = Block.box(2.88F, 2.88F, 5.92F, 13.12F, 12.96F, 16F);
	private static final VoxelShape STOOL_SOUTH = Block.box(2.88F, 2.88F, 0.0F, 13.12F, 12.96F, 10.08F);
	private static final VoxelShape STOOL_WEST = Block.box(5.92F, 2.88F, 2.88F, 16F, 12.96F, 13.12F);
	private static final VoxelShape STOOL_EAST = Block.box(0.0F, 2.88F, 2.88F, 10.08F, 12.96F, 13.12F);
	private static final VoxelShape COLLISION_STOOL_FLOOR = Block.box(2.88F, 0.0F, 2.88F, 13.12F, 9.6F, 13.12F);

	public ColorizerStoolBlock() {
		this.registerDefaultState(defaultBlockState().setValue(UP, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(UP);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState stoolState = super.getStateForPlacement(context);
		
		if(stoolState == null) {
			return this.defaultBlockState();
		}
		return stoolState.setValue(UP, isPotUp(stoolState, context.getLevel(), context.getClickedPos()));
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		BlockState state = super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		return state.getBlock() == this ? state.setValue(UP, isPotUp(stateIn, worldIn, currentPos)) : state;
	}

	private boolean isPotUp(BlockState stoolState, BlockGetter world, BlockPos pos) {
		return world.getBlockState(pos.above()).getBlock() == DecorBlocks.PLANTER_POT.get() && stoolState.getValue(FACE) == AttachFace.FLOOR;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (state.getValue(UP)) {
			return POT_STOOL;
		}

		switch (getFacing(state)) {
		case DOWN:
			return STOOL_CEILING;
		case EAST:
			return STOOL_EAST;
		case NORTH:
			return STOOL_NORTH;
		case SOUTH:
			return STOOL_SOUTH;
		case WEST:
			return STOOL_WEST;
		case UP:
		default:
			return STOOL_FLOOR;
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (state.getValue(UP)) {
			return POT_STOOL;
		}

		switch (getFacing(state)) {
		case DOWN:
			return STOOL_CEILING;
		case EAST:
			return STOOL_EAST;
		case NORTH:
			return STOOL_NORTH;
		case SOUTH:
			return STOOL_SOUTH;
		case WEST:
			return STOOL_WEST;
		case UP:
		default:
			return COLLISION_STOOL_FLOOR;
		}
	}

}

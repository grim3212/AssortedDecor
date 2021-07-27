package com.grim3212.assorted.decor.common.block.colorizer;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.core.Direction;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;

public class ColorizerTableBlock extends ColorizerSideBlock {

	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty DOWN = BooleanProperty.create("down");
	public static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = Util.make(Maps.newEnumMap(Direction.class), (map) -> {
		map.put(Direction.NORTH, NORTH);
		map.put(Direction.EAST, EAST);
		map.put(Direction.SOUTH, SOUTH);
		map.put(Direction.WEST, WEST);
		map.put(Direction.UP, UP);
		map.put(Direction.DOWN, DOWN);
	});

	public ColorizerTableBlock() {
		this.registerDefaultState(defaultBlockState().setValue(NORTH, false).setValue(SOUTH, false).setValue(WEST, false).setValue(EAST, false).setValue(UP, false).setValue(DOWN, false));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		switch (getFacing(state)) {
		case DOWN:
			return ColorizerCounterBlock.COUNTER_CEILING;
		case EAST:
			return ColorizerCounterBlock.COUNTER_EAST;
		case NORTH:
			return ColorizerCounterBlock.COUNTER_NORTH;
		case SOUTH:
			return ColorizerCounterBlock.COUNTER_SOUTH;
		case WEST:
			return ColorizerCounterBlock.COUNTER_WEST;
		case UP:
		default:
			return ColorizerCounterBlock.COUNTER_FLOOR;
		}
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState tableState = super.getStateForPlacement(context);
		
		if(tableState == null) {
			return this.defaultBlockState();
		}
		
		BlockPos blockpos = context.getClickedPos();
		BlockPos northPos = blockpos.north();
		BlockPos eastPos = blockpos.east();
		BlockPos southPos = blockpos.south();
		BlockPos westPos = blockpos.west();
		BlockPos upPos = blockpos.above();
		BlockPos downPos = blockpos.below();
		return tableState.setValue(NORTH, this.canConnectTo(context.getLevel(), blockpos, northPos)).setValue(EAST, this.canConnectTo(context.getLevel(), blockpos, eastPos)).setValue(SOUTH, this.canConnectTo(context.getLevel(), blockpos, southPos)).setValue(WEST, this.canConnectTo(context.getLevel(), blockpos, westPos)).setValue(UP, this.canConnectTo(context.getLevel(), blockpos, upPos)).setValue(DOWN, this.canConnectTo(context.getLevel(), blockpos, downPos));
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		BlockState superState = super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		return superState.getBlock() == this ? superState.setValue(FACING_TO_PROPERTY_MAP.get(facing), this.canConnectTo(worldIn, currentPos, facingPos)) : superState;
	}

	public boolean canConnectTo(BlockGetter worldIn, BlockPos currentPos, BlockPos placePos) {
		BlockState currentState = worldIn.getBlockState(currentPos);
		BlockState placeState = worldIn.getBlockState(placePos);

		if (currentState.getBlock() instanceof ColorizerTableBlock && placeState.getBlock() instanceof ColorizerTableBlock) {
			if (currentState.getBlock() == currentState.getBlock() && currentState.getValue(FACE) == placeState.getValue(FACE)) {
				return currentState.getValue(FACE) == AttachFace.WALL ? currentState.getValue(HORIZONTAL_FACING) == placeState.getValue(HORIZONTAL_FACING) : true;
			}
		}
		return false;
	}

}

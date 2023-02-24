package com.grim3212.assorted.decor.common.blocks;

import com.grim3212.assorted.decor.common.blocks.blockentity.WallClockBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallClockBlock extends HorizontalDirectionalBlock implements EntityBlock {

	protected static final VoxelShape CLOCK_NORTH_AABB = Block.box(0f, 0f, 14f, 16f, 16f, 16f);
	protected static final VoxelShape CLOCK_SOUTH_AABB = Block.box(0f, 0f, 0f, 16f, 16f, 2f);
	protected static final VoxelShape CLOCK_WEST_AABB = Block.box(14f, 0f, 0f, 16f, 16f, 16f);
	protected static final VoxelShape CLOCK_EAST_AABB = Block.box(0f, 0f, 0f, 2f, 16f, 16f);
	public static final IntegerProperty TIME = IntegerProperty.create("time", 0, 63);

	protected WallClockBlock(Properties builder) {
		super(builder);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TIME, 0));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, TIME);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext ctx) {
		switch (state.getValue(FACING)) {
			case EAST:
				return CLOCK_EAST_AABB;
			case WEST:
				return CLOCK_WEST_AABB;
			case SOUTH:
				return CLOCK_SOUTH_AABB;
			case NORTH:
				return CLOCK_NORTH_AABB;
			default:
				return CLOCK_NORTH_AABB;
		}
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
		if (worldIn.getBlockState(pos.west()).isSolidRender(worldIn, pos)) {
			return true;
		}
		if (worldIn.getBlockState(pos.east()).isSolidRender(worldIn, pos)) {
			return true;
		}
		if (worldIn.getBlockState(pos.north()).isSolidRender(worldIn, pos)) {
			return true;
		} else {
			return worldIn.getBlockState(pos.south()).isSolidRender(worldIn, pos);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = this.defaultBlockState();
		Direction facing = context.getClickedFace();
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();

		if (facing == Direction.NORTH && world.getBlockState(pos.south()).isSolidRender(world, pos)) {
			state = state.setValue(FACING, Direction.NORTH);
		}
		if (facing == Direction.SOUTH && world.getBlockState(pos.north()).isSolidRender(world, pos)) {
			state = state.setValue(FACING, Direction.SOUTH);
		}
		if (facing == Direction.WEST && world.getBlockState(pos.east()).isSolidRender(world, pos)) {
			state = state.setValue(FACING, Direction.WEST);
		}
		if (facing == Direction.EAST && world.getBlockState(pos.west()).isSolidRender(world, pos)) {
			state = state.setValue(FACING, Direction.EAST);
		}

		return state;
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flg) {
		Direction facing = state.getValue(FACING);
		boolean flag = false;
		if (facing == Direction.NORTH && worldIn.getBlockState(pos.south()).isSolidRender(worldIn, pos)) {
			flag = true;
		}
		if (facing == Direction.SOUTH && worldIn.getBlockState(pos.north()).isSolidRender(worldIn, pos)) {
			flag = true;
		}
		if (facing == Direction.WEST && worldIn.getBlockState(pos.east()).isSolidRender(worldIn, pos)) {
			flag = true;
		}
		if (facing == Direction.EAST && worldIn.getBlockState(pos.west()).isSolidRender(worldIn, pos)) {
			flag = true;
		}
		if (!flag) {
			worldIn.destroyBlock(pos, true);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new WallClockBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return (level1, blockPos, blockState, t) -> {
			if (t instanceof WallClockBlockEntity wallclock) {
				wallclock.tick();
			}
		};
	}
}

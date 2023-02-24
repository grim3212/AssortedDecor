package com.grim3212.assorted.decor.common.blocks;

import com.grim3212.assorted.decor.common.blocks.blockentity.CalendarBlockEntity;

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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CalendarBlock extends HorizontalDirectionalBlock implements EntityBlock {

	protected static final VoxelShape CALENDAR_NORTH_AABB = Block.box(4f, 2.08f, 14.96f, 12f, 14.96f, 16f);
	protected static final VoxelShape CALENDAR_SOUTH_AABB = Block.box(4f, 2.08f, 0f, 12f, 14.96f, 1.04f);
	protected static final VoxelShape CALENDAR_WEST_AABB = Block.box(14.96f, 2.08f, 4f, 16f, 14.96f, 12f);
	protected static final VoxelShape CALENDAR_EAST_AABB = Block.box(0f, 2.08f, 4f, 1.04f, 14.96f, 12f);

	protected CalendarBlock(Properties builder) {
		super(builder);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		switch (state.getValue(FACING)) {
			case EAST:
				return CALENDAR_EAST_AABB;
			case WEST:
				return CALENDAR_WEST_AABB;
			case SOUTH:
				return CALENDAR_SOUTH_AABB;
			case NORTH:
				return CALENDAR_NORTH_AABB;
			default:
				return CALENDAR_NORTH_AABB;
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CalendarBlockEntity(pos, state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		for (Direction enumfacing : Direction.Plane.HORIZONTAL) {
			if (this.canBlockStay(context.getLevel(), context.getClickedPos(), enumfacing)) {
				return this.defaultBlockState().setValue(FACING, enumfacing);
			}
		}

		return this.defaultBlockState();
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
		for (Direction enumfacing : FACING.getPossibleValues()) {
			if (this.canBlockStay(worldIn, pos, enumfacing)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
		Direction enumfacing = state.getValue(FACING);

		if (!this.canBlockStay(worldIn, pos, enumfacing)) {
			worldIn.destroyBlock(pos, true);
		}
	}

	protected boolean canBlockStay(LevelReader worldIn, BlockPos pos, Direction facing) {
		BlockPos blockpos = pos.relative(facing.getOpposite());
		return Block.canSupportCenter(worldIn, blockpos, facing);
	}
}

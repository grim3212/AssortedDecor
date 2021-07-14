package com.grim3212.assorted.decor.common.block;

import com.grim3212.assorted.decor.common.block.tileentity.CalendarTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class CalendarBlock extends HorizontalBlock {

	protected static final VoxelShape CALENDAR_NORTH_AABB = Block.box(4f, 2.08f, 14.96f, 12f, 14.96f, 16f);
	protected static final VoxelShape CALENDAR_SOUTH_AABB = Block.box(4f, 2.08f, 0f, 12f, 14.96f, 1.04f);
	protected static final VoxelShape CALENDAR_WEST_AABB = Block.box(14.96f, 2.08f, 4f, 16f, 14.96f, 12f);
	protected static final VoxelShape CALENDAR_EAST_AABB = Block.box(0f, 2.08f, 4f, 1.04f, 14.96f, 12f);

	protected CalendarBlock(Properties builder) {
		super(builder);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
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
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new CalendarTileEntity();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		for (Direction enumfacing : Direction.Plane.HORIZONTAL) {
			if (this.canBlockStay(context.getLevel(), context.getClickedPos(), enumfacing)) {
				return this.defaultBlockState().setValue(FACING, enumfacing);
			}
		}

		return this.defaultBlockState();
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
		for (Direction enumfacing : FACING.getPossibleValues()) {
			if (this.canBlockStay(worldIn, pos, enumfacing)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
		Direction enumfacing = state.getValue(FACING);

		if (!this.canBlockStay(worldIn, pos, enumfacing)) {
			worldIn.destroyBlock(pos, true);
		}
	}

	protected boolean canBlockStay(IWorldReader worldIn, BlockPos pos, Direction facing) {
		BlockPos blockpos = pos.relative(facing.getOpposite());
		return Block.canSupportCenter(worldIn, blockpos, facing);
	}
}

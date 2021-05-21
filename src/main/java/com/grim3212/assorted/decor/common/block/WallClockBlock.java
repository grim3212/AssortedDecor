package com.grim3212.assorted.decor.common.block;

import com.grim3212.assorted.decor.common.block.tileentity.WallClockTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class WallClockBlock extends HorizontalBlock {

	protected static final VoxelShape CLOCK_NORTH_AABB = Block.makeCuboidShape(0f, 0f, 14f, 16f, 16f, 16f);
	protected static final VoxelShape CLOCK_SOUTH_AABB = Block.makeCuboidShape(0f, 0f, 0f, 16f, 16f, 2f);
	protected static final VoxelShape CLOCK_WEST_AABB = Block.makeCuboidShape(14f, 0f, 0f, 16f, 16f, 16f);
	protected static final VoxelShape CLOCK_EAST_AABB = Block.makeCuboidShape(0f, 0f, 0f, 2f, 16f, 16f);
	public static final IntegerProperty TIME = IntegerProperty.create("time", 0, 63);

	protected WallClockBlock(Properties builder) {
		super(builder);
		this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(TIME, 0));
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING, TIME);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext ctx) {
		switch (state.get(HORIZONTAL_FACING)) {
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
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		if (worldIn.getBlockState(pos.west()).isOpaqueCube(worldIn, pos)) {
			return true;
		}
		if (worldIn.getBlockState(pos.east()).isOpaqueCube(worldIn, pos)) {
			return true;
		}
		if (worldIn.getBlockState(pos.north()).isOpaqueCube(worldIn, pos)) {
			return true;
		} else {
			return worldIn.getBlockState(pos.south()).isOpaqueCube(worldIn, pos);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = this.getDefaultState();
		Direction facing = context.getFace();
		World world = context.getWorld();
		BlockPos pos = context.getPos();

		if (facing == Direction.NORTH && world.getBlockState(pos.south()).isOpaqueCube(world, pos)) {
			state = state.with(HORIZONTAL_FACING, Direction.NORTH);
		}
		if (facing == Direction.SOUTH && world.getBlockState(pos.north()).isOpaqueCube(world, pos)) {
			state = state.with(HORIZONTAL_FACING, Direction.SOUTH);
		}
		if (facing == Direction.WEST && world.getBlockState(pos.east()).isOpaqueCube(world, pos)) {
			state = state.with(HORIZONTAL_FACING, Direction.WEST);
		}
		if (facing == Direction.EAST && world.getBlockState(pos.west()).isOpaqueCube(world, pos)) {
			state = state.with(HORIZONTAL_FACING, Direction.EAST);
		}

		return state;
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flg) {
		Direction facing = state.get(HORIZONTAL_FACING);
		boolean flag = false;
		if (facing == Direction.NORTH && worldIn.getBlockState(pos.south()).isOpaqueCube(worldIn, pos)) {
			flag = true;
		}
		if (facing == Direction.SOUTH && worldIn.getBlockState(pos.north()).isOpaqueCube(worldIn, pos)) {
			flag = true;
		}
		if (facing == Direction.WEST && worldIn.getBlockState(pos.east()).isOpaqueCube(worldIn, pos)) {
			flag = true;
		}
		if (facing == Direction.EAST && worldIn.getBlockState(pos.west()).isOpaqueCube(worldIn, pos)) {
			flag = true;
		}
		if (!flag) {
			worldIn.destroyBlock(pos, true);
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new WallClockTileEntity();
	}
}

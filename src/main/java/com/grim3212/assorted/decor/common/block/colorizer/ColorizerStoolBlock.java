package com.grim3212.assorted.decor.common.block.colorizer;

import com.grim3212.assorted.decor.common.block.DecorBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class ColorizerStoolBlock extends ColorizerSideBlock {

	public static final BooleanProperty UP = BooleanProperty.create("up");

	public static final VoxelShape POT_STOOL = Block.makeCuboidShape(2.88F, 0.0F, 2.88F, 13.12F, 16F, 13.12F);
	private static final VoxelShape STOOL_FLOOR = Block.makeCuboidShape(2.88F, 0.0F, 2.88F, 13.12F, 10.08F, 13.12F);
	private static final VoxelShape STOOL_CEILING = Block.makeCuboidShape(2.88F, 16F, 2.88F, 13.12F, 5.92F, 13.12F);
	private static final VoxelShape STOOL_NORTH = Block.makeCuboidShape(2.88F, 2.88F, 5.92F, 13.12F, 12.96F, 16F);
	private static final VoxelShape STOOL_SOUTH = Block.makeCuboidShape(2.88F, 2.88F, 0.0F, 13.12F, 12.96F, 10.08F);
	private static final VoxelShape STOOL_WEST = Block.makeCuboidShape(5.92F, 2.88F, 2.88F, 16F, 12.96F, 13.12F);
	private static final VoxelShape STOOL_EAST = Block.makeCuboidShape(0.0F, 2.88F, 2.88F, 10.08F, 12.96F, 13.12F);
	private static final VoxelShape COLLISION_STOOL_FLOOR = Block.makeCuboidShape(2.88F, 0.0F, 2.88F, 13.12F, 9.6F, 13.12F);

	public ColorizerStoolBlock() {
		this.setDefaultState(getDefaultState().with(UP, false));
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(UP);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState stoolState = super.getStateForPlacement(context);
		
		if(stoolState == null) {
			return this.getDefaultState();
		}
		return stoolState.with(UP, isPotUp(stoolState, context.getWorld(), context.getPos()));
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos).with(UP, isPotUp(stateIn, worldIn, currentPos));
	}

	private boolean isPotUp(BlockState stoolState, IBlockReader world, BlockPos pos) {
		return world.getBlockState(pos.up()).getBlock() == DecorBlocks.PLANTER_POT.get() && stoolState.get(FACE) == AttachFace.FLOOR;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (state.get(UP)) {
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
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (state.get(UP)) {
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

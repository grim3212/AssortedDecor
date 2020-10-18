package com.grim3212.assorted.decor.common.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.Half;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class ColorizerTrapDoorBlock extends ColorizerRotateBlock {

	protected static final VoxelShape EAST_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_OPEN_AABB = Block.makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape SOUTH_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
	protected static final VoxelShape NORTH_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape BOTTOM_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
	protected static final VoxelShape TOP_AABB = Block.makeCuboidShape(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);

	public ColorizerTrapDoorBlock() {
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(TrapDoorBlock.OPEN, false).with(HALF, Half.BOTTOM).with(TrapDoorBlock.POWERED, false).with(WATERLOGGED, false));
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(TrapDoorBlock.OPEN, TrapDoorBlock.POWERED);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (!state.get(TrapDoorBlock.OPEN)) {
			return state.get(HALF) == Half.TOP ? TOP_AABB : BOTTOM_AABB;
		} else {
			switch ((Direction) state.get(FACING)) {
			case NORTH:
			default:
				return NORTH_OPEN_AABB;
			case SOUTH:
				return SOUTH_OPEN_AABB;
			case WEST:
				return WEST_OPEN_AABB;
			case EAST:
				return EAST_OPEN_AABB;
			}
		}
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		switch (type) {
		case LAND:
			return state.get(TrapDoorBlock.OPEN);
		case WATER:
			return state.get(WATERLOGGED);
		case AIR:
			return state.get(TrapDoorBlock.OPEN);
		default:
			return false;
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		ItemStack heldItem = player.getHeldItem(handIn);

		if (!heldItem.isEmpty()) {
			// if (heldItem.getItem() == DecorItems.brush) {
			// if (this.tryUseBrush(worldIn, player, handIn, pos)) {
			// return true;
			// }
			// }

			Block block = Block.getBlockFromItem(heldItem.getItem());
			if (block != Blocks.AIR) {
				if (super.onBlockActivated(state, worldIn, pos, player, handIn, hit).isSuccess()) {
					return ActionResultType.SUCCESS;
				}
			}
		}

		state = state.func_235896_a_(TrapDoorBlock.OPEN);
		worldIn.setBlockState(pos, state, 2);
		if (state.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}

		this.playSound(player, worldIn, pos, state.get(TrapDoorBlock.OPEN));
		return ActionResultType.func_233537_a_(worldIn.isRemote);
	}

	protected void playSound(@Nullable PlayerEntity player, World worldIn, BlockPos pos, boolean p_185731_4_) {
		if (p_185731_4_) {
			int i = this.material == Material.IRON ? 1037 : 1007;
			worldIn.playEvent(player, i, pos, 0);
		} else {
			int j = this.material == Material.IRON ? 1036 : 1013;
			worldIn.playEvent(player, j, pos, 0);
		}

	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (!worldIn.isRemote) {
			boolean flag = worldIn.isBlockPowered(pos);
			if (flag != state.get(TrapDoorBlock.POWERED)) {
				if (state.get(TrapDoorBlock.OPEN) != flag) {
					state = state.with(TrapDoorBlock.OPEN, flag);
					this.playSound((PlayerEntity) null, worldIn, pos, flag);
				}

				worldIn.setBlockState(pos, state.with(TrapDoorBlock.POWERED, flag), 2);
				if (state.get(WATERLOGGED)) {
					worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
				}
			}

		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState blockstate = this.getDefaultState();
		FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
		Direction direction = context.getFace();
		if (!context.replacingClickedOnBlock() && direction.getAxis().isHorizontal()) {
			blockstate = blockstate.with(FACING, direction).with(HALF, context.getHitVec().y - (double) context.getPos().getY() > 0.5D ? Half.TOP : Half.BOTTOM);
		} else {
			blockstate = blockstate.with(FACING, context.getPlacementHorizontalFacing().getOpposite()).with(HALF, direction == Direction.UP ? Half.BOTTOM : Half.TOP);
		}

		if (context.getWorld().isBlockPowered(context.getPos())) {
			blockstate = blockstate.with(TrapDoorBlock.OPEN, true).with(TrapDoorBlock.POWERED, true);
		}

		return blockstate.with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}

		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
		if (state.get(TrapDoorBlock.OPEN)) {
			BlockState down = world.getBlockState(pos.down());
			if (down.getBlock() == Blocks.LADDER)
				return down.get(LadderBlock.FACING) == state.get(FACING);
		}
		return false;
	}
}

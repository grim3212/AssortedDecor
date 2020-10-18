package com.grim3212.assorted.decor.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ColorizerDoorBlock extends ColorizerBlock {

	protected static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
	protected static final VoxelShape NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_AABB = Block.makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

	public ColorizerDoorBlock() {
		this.setDefaultState(this.stateContainer.getBaseState().with(DoorBlock.FACING, Direction.NORTH).with(DoorBlock.OPEN, false).with(DoorBlock.HINGE, DoorHingeSide.LEFT).with(DoorBlock.POWERED, false).with(DoorBlock.HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Direction direction = state.get(DoorBlock.FACING);
		boolean flag = !state.get(DoorBlock.OPEN);
		boolean flag1 = state.get(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
		switch (direction) {
		case EAST:
		default:
			return flag ? EAST_AABB : (flag1 ? NORTH_AABB : SOUTH_AABB);
		case SOUTH:
			return flag ? SOUTH_AABB : (flag1 ? EAST_AABB : WEST_AABB);
		case WEST:
			return flag ? WEST_AABB : (flag1 ? SOUTH_AABB : NORTH_AABB);
		case NORTH:
			return flag ? NORTH_AABB : (flag1 ? WEST_AABB : EAST_AABB);
		}
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf doubleblockhalf = stateIn.get(DoorBlock.HALF);
		if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
			return facingState.isIn(this) && facingState.get(DoorBlock.HALF) != doubleblockhalf ? stateIn.with(DoorBlock.FACING, facingState.get(DoorBlock.FACING)).with(DoorBlock.OPEN, facingState.get(DoorBlock.OPEN)).with(DoorBlock.HINGE, facingState.get(DoorBlock.HINGE)).with(DoorBlock.POWERED, facingState.get(DoorBlock.POWERED)) : Blocks.AIR.getDefaultState();
		} else {
			return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		}
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!worldIn.isRemote && player.isCreative()) {
			DoubleBlockHalf doubleblockhalf = state.get(DoorBlock.HALF);
			if (doubleblockhalf == DoubleBlockHalf.UPPER) {
				BlockPos blockpos = pos.down();
				BlockState blockstate = worldIn.getBlockState(blockpos);
				if (blockstate.getBlock() == state.getBlock() && blockstate.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
					worldIn.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
				}
			}
		}

		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		switch (type) {
		case LAND:
			return state.get(DoorBlock.OPEN);
		case WATER:
			return false;
		case AIR:
			return state.get(DoorBlock.OPEN);
		default:
			return false;
		}
	}

	private int getCloseSound() {
		return 1012;
	}

	private int getOpenSound() {
		return 1006;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockPos blockpos = context.getPos();
		if (blockpos.getY() < 255 && context.getWorld().getBlockState(blockpos.up()).isReplaceable(context)) {
			World world = context.getWorld();
			boolean flag = world.isBlockPowered(blockpos) || world.isBlockPowered(blockpos.up());
			return this.getDefaultState().with(DoorBlock.FACING, context.getPlacementHorizontalFacing()).with(DoorBlock.HINGE, this.getHingeSide(context)).with(DoorBlock.POWERED, flag).with(DoorBlock.OPEN, flag).with(DoorBlock.HALF, DoubleBlockHalf.LOWER);
		} else {
			return null;
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		worldIn.setBlockState(pos.up(), state.with(DoorBlock.HALF, DoubleBlockHalf.UPPER), 3);
	}

	private DoorHingeSide getHingeSide(BlockItemUseContext context) {
		IBlockReader iblockreader = context.getWorld();
		BlockPos blockpos = context.getPos();
		Direction direction = context.getPlacementHorizontalFacing();
		BlockPos blockpos1 = blockpos.up();
		Direction direction1 = direction.rotateYCCW();
		BlockPos blockpos2 = blockpos.offset(direction1);
		BlockState blockstate = iblockreader.getBlockState(blockpos2);
		BlockPos blockpos3 = blockpos1.offset(direction1);
		BlockState blockstate1 = iblockreader.getBlockState(blockpos3);
		Direction direction2 = direction.rotateY();
		BlockPos blockpos4 = blockpos.offset(direction2);
		BlockState blockstate2 = iblockreader.getBlockState(blockpos4);
		BlockPos blockpos5 = blockpos1.offset(direction2);
		BlockState blockstate3 = iblockreader.getBlockState(blockpos5);
		int i = (blockstate.hasOpaqueCollisionShape(iblockreader, blockpos2) ? -1 : 0) + (blockstate1.hasOpaqueCollisionShape(iblockreader, blockpos3) ? -1 : 0) + (blockstate2.hasOpaqueCollisionShape(iblockreader, blockpos4) ? 1 : 0) + (blockstate3.hasOpaqueCollisionShape(iblockreader, blockpos5) ? 1 : 0);
		boolean flag = blockstate.isIn(this) && blockstate.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
		boolean flag1 = blockstate2.isIn(this) && blockstate2.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
		if ((!flag || flag1) && i <= 0) {
			if ((!flag1 || flag) && i >= 0) {
				int j = direction.getXOffset();
				int k = direction.getZOffset();
				Vector3d vector3d = context.getHitVec();
				double d0 = vector3d.x - (double) blockpos.getX();
				double d1 = vector3d.z - (double) blockpos.getZ();
				return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D)) && (k <= 0 || !(d0 < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
			} else {
				return DoorHingeSide.LEFT;
			}
		} else {
			return DoorHingeSide.RIGHT;
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
					if (state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
						if (worldIn.getBlockState(pos.up()).getBlock() == this)
							return super.onBlockActivated(state, worldIn, pos.up(), player, handIn, hit);
					} else {
						if (worldIn.getBlockState(pos.down()).getBlock() == this)
							return super.onBlockActivated(state, worldIn, pos.down(), player, handIn, hit);
					}
				}
			}
		}

		state = state.func_235896_a_(DoorBlock.OPEN);
		worldIn.setBlockState(pos, state, 10);
		worldIn.playEvent(player, state.get(DoorBlock.OPEN) ? this.getOpenSound() : this.getCloseSound(), pos, 0);
		return ActionResultType.func_233537_a_(worldIn.isRemote);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.offset(state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
		if (blockIn != this && flag != state.get(DoorBlock.POWERED)) {
			if (flag != state.get(DoorBlock.OPEN)) {
				this.playSound(worldIn, pos, flag);
			}

			worldIn.setBlockState(pos, state.with(DoorBlock.POWERED, flag).with(DoorBlock.OPEN, flag), 2);
		}

	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockPos blockpos = pos.down();
		BlockState blockstate = worldIn.getBlockState(blockpos);
		return state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? blockstate.isSolidSide(worldIn, blockpos, Direction.UP) : blockstate.isIn(this);
	}

	private void playSound(World worldIn, BlockPos pos, boolean isOpening) {
		worldIn.playEvent((PlayerEntity) null, isOpening ? this.getOpenSound() : this.getCloseSound(), pos, 0);
	}

	@Override
	public PushReaction getPushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(DoorBlock.FACING, rot.rotate(state.get(DoorBlock.FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return mirrorIn == Mirror.NONE ? state : state.rotate(mirrorIn.toRotation(state.get(DoorBlock.FACING))).func_235896_a_(DoorBlock.HINGE);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public long getPositionRandom(BlockState state, BlockPos pos) {
		return MathHelper.getCoordinateRandom(pos.getX(), pos.down(state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(DoorBlock.HALF, DoorBlock.FACING, DoorBlock.OPEN, DoorBlock.HINGE, DoorBlock.POWERED);
	}
}

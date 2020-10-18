package com.grim3212.assorted.decor.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ColorizerFenceGateBlock extends ColorizerHorizontalBlock {

	protected static final VoxelShape AABB_HITBOX_ZAXIS = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
	protected static final VoxelShape AABB_HITBOX_XAXIS = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
	protected static final VoxelShape AABB_HITBOX_ZAXIS_INWALL = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 13.0D, 10.0D);
	protected static final VoxelShape AABB_HITBOX_XAXIS_INWALL = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 13.0D, 16.0D);
	protected static final VoxelShape field_208068_x = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 24.0D, 10.0D);
	protected static final VoxelShape AABB_COLLISION_BOX_XAXIS = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 24.0D, 16.0D);
	protected static final VoxelShape AABB_RENDER_BOX_ZAXIS = VoxelShapes.or(Block.makeCuboidShape(0.0D, 5.0D, 7.0D, 2.0D, 16.0D, 9.0D), Block.makeCuboidShape(14.0D, 5.0D, 7.0D, 16.0D, 16.0D, 9.0D));
	protected static final VoxelShape AABB_COLLISION_BOX_ZAXIS = VoxelShapes.or(Block.makeCuboidShape(7.0D, 5.0D, 0.0D, 9.0D, 16.0D, 2.0D), Block.makeCuboidShape(7.0D, 5.0D, 14.0D, 9.0D, 16.0D, 16.0D));
	protected static final VoxelShape AABB_RENDER_BOX_ZAXIS_INWALL = VoxelShapes.or(Block.makeCuboidShape(0.0D, 2.0D, 7.0D, 2.0D, 13.0D, 9.0D), Block.makeCuboidShape(14.0D, 2.0D, 7.0D, 16.0D, 13.0D, 9.0D));
	protected static final VoxelShape AABB_RENDER_BOX_XAXIS_INWALL = VoxelShapes.or(Block.makeCuboidShape(7.0D, 2.0D, 0.0D, 9.0D, 13.0D, 2.0D), Block.makeCuboidShape(7.0D, 2.0D, 14.0D, 9.0D, 13.0D, 16.0D));

	public ColorizerFenceGateBlock() {
		this.setDefaultState(this.stateContainer.getBaseState().with(FenceGateBlock.OPEN, false).with(FenceGateBlock.POWERED, false).with(FenceGateBlock.IN_WALL, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (state.get(FenceGateBlock.IN_WALL)) {
			return state.get(HORIZONTAL_FACING).getAxis() == Direction.Axis.X ? AABB_HITBOX_XAXIS_INWALL : AABB_HITBOX_ZAXIS_INWALL;
		} else {
			return state.get(HORIZONTAL_FACING).getAxis() == Direction.Axis.X ? AABB_HITBOX_XAXIS : AABB_HITBOX_ZAXIS;
		}
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		Direction.Axis direction$axis = facing.getAxis();
		if (stateIn.get(HORIZONTAL_FACING).rotateY().getAxis() != direction$axis) {
			return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		} else {
			boolean flag = this.isWall(facingState) || this.isWall(worldIn.getBlockState(currentPos.offset(facing.getOpposite())));
			return stateIn.with(FenceGateBlock.IN_WALL, flag);
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (state.get(FenceGateBlock.OPEN)) {
			return VoxelShapes.empty();
		} else {
			return state.get(HORIZONTAL_FACING).getAxis() == Direction.Axis.Z ? field_208068_x : AABB_COLLISION_BOX_XAXIS;
		}
	}

	@Override
	public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		if (state.get(FenceGateBlock.IN_WALL)) {
			return state.get(HORIZONTAL_FACING).getAxis() == Direction.Axis.X ? AABB_RENDER_BOX_XAXIS_INWALL : AABB_RENDER_BOX_ZAXIS_INWALL;
		} else {
			return state.get(HORIZONTAL_FACING).getAxis() == Direction.Axis.X ? AABB_COLLISION_BOX_ZAXIS : AABB_RENDER_BOX_ZAXIS;
		}
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		switch (type) {
		case LAND:
			return state.get(FenceGateBlock.OPEN);
		case WATER:
			return false;
		case AIR:
			return state.get(FenceGateBlock.OPEN);
		default:
			return false;
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		World world = context.getWorld();
		BlockPos blockpos = context.getPos();
		boolean flag = world.isBlockPowered(blockpos);
		Direction direction = context.getPlacementHorizontalFacing();
		Direction.Axis direction$axis = direction.getAxis();
		boolean flag1 = direction$axis == Direction.Axis.Z && (this.isWall(world.getBlockState(blockpos.west())) || this.isWall(world.getBlockState(blockpos.east()))) || direction$axis == Direction.Axis.X && (this.isWall(world.getBlockState(blockpos.north())) || this.isWall(world.getBlockState(blockpos.south())));
		return this.getDefaultState().with(HORIZONTAL_FACING, direction).with(FenceGateBlock.OPEN, flag).with(FenceGateBlock.POWERED, flag).with(FenceGateBlock.IN_WALL, flag1);
	}

	private boolean isWall(BlockState state) {
		return state.getBlock().isIn(BlockTags.WALLS);
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

		if (state.get(FenceGateBlock.OPEN)) {
			state = state.with(FenceGateBlock.OPEN, false);
			worldIn.setBlockState(pos, state, 10);
		} else {
			Direction direction = player.getHorizontalFacing();
			if (state.get(HORIZONTAL_FACING) == direction.getOpposite()) {
				state = state.with(HORIZONTAL_FACING, direction);
			}

			state = state.with(FenceGateBlock.OPEN, true);
			worldIn.setBlockState(pos, state, 10);
		}

		worldIn.playEvent(player, state.get(FenceGateBlock.OPEN) ? 1008 : 1014, pos, 0);
		return ActionResultType.func_233537_a_(worldIn.isRemote);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (!worldIn.isRemote) {
			boolean flag = worldIn.isBlockPowered(pos);
			if (state.get(FenceGateBlock.POWERED) != flag) {
				worldIn.setBlockState(pos, state.with(FenceGateBlock.POWERED, flag).with(FenceGateBlock.OPEN, flag), 2);
				if (state.get(FenceGateBlock.OPEN) != flag) {
					worldIn.playEvent((PlayerEntity) null, flag ? 1008 : 1014, pos, 0);
				}
			}

		}
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING, FenceGateBlock.OPEN, FenceGateBlock.POWERED, FenceGateBlock.IN_WALL);
	}
}

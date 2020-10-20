package com.grim3212.assorted.decor.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ColorizerLampPost extends ColorizerBlock implements IWaterLoggable {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final EnumProperty<LampPart> PART = EnumProperty.create("part", LampPart.class);

	private static final VoxelShape TOP = Block.makeCuboidShape(2F, 0.0F, 2F, 14F, 10.96F, 14F);
	private static final VoxelShape MIDDLE = Block.makeCuboidShape(6F, 0.0F, 6F, 10F, 16F, 10F);
	private static final VoxelShape BOTTOM = Block.makeCuboidShape(6F, 0.0F, 6F, 10F, 16F, 10F);

	public ColorizerLampPost() {
		this.setDefaultState(this.stateContainer.getBaseState().with(PART, LampPart.BOTTOM).with(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (state.get(PART) == LampPart.TOP) {
			return TOP;
		} else if (state.get(PART) == LampPart.MIDDLE) {
			return MIDDLE;
		} else {
			return BOTTOM;
		}
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return state.get(PART) == LampPart.TOP ? 15 : super.getLightValue(state, world, pos);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockPos blockpos = context.getPos();
		FluidState fluidstate = context.getWorld().getFluidState(blockpos);
		return this.getDefaultState().with(PART, LampPart.BOTTOM).with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(PART, WATERLOGGED);
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}

		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
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
					if (state.get(PART) == LampPart.BOTTOM) {
						if (worldIn.getBlockState(pos.up()).getBlock() == this && worldIn.getBlockState(pos.up(2)).getBlock() == this) {
							// Middle
							super.onBlockActivated(state, worldIn, pos.up(), player, handIn, hit);
							// Top
							return super.onBlockActivated(state, worldIn, pos.up(2), player, handIn, hit);
						}

					} else if (state.get(PART) == LampPart.MIDDLE) {
						if (worldIn.getBlockState(pos.up()).getBlock() == this && worldIn.getBlockState(pos.down()).getBlock() == this) {
							// Bottom
							super.onBlockActivated(state, worldIn, pos.down(), player, handIn, hit);
							// Top
							return super.onBlockActivated(state, worldIn, pos.up(), player, handIn, hit);
						}
					} else if (state.get(PART) == LampPart.TOP) {
						if (worldIn.getBlockState(pos.down()).getBlock() == this && worldIn.getBlockState(pos.down(2)).getBlock() == this) {
							// Middle
							super.onBlockActivated(state, worldIn, pos.down(), player, handIn, hit);
							// Bottom
							return super.onBlockActivated(state, worldIn, pos.down(2), player, handIn, hit);
						}
					}
				}
			}
		}

		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		worldIn.setBlockState(pos.up(), state.with(PART, LampPart.MIDDLE), 3);
		worldIn.setBlockState(pos.up(2), state.with(PART, LampPart.TOP), 3);
	}

	@Override
	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
		if (state.get(PART) == LampPart.BOTTOM) {
			worldIn.removeBlock(pos.up(), false);
			worldIn.removeBlock(pos.up(2), false);
		} else if (state.get(PART) == LampPart.MIDDLE) {
			worldIn.removeBlock(pos.up(), false);
			worldIn.removeBlock(pos.down(), false);
		} else if (state.get(PART) == LampPart.TOP) {
			worldIn.removeBlock(pos.down(), false);
			worldIn.removeBlock(pos.down(2), false);
		}

		super.onPlayerDestroy(worldIn, pos, state);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBlockHarvested(worldIn, pos, state, player);

		if (state.get(PART) == LampPart.BOTTOM) {
			worldIn.removeBlock(pos.up(), false);
			worldIn.removeBlock(pos.up(2), false);
		} else if (state.get(PART) == LampPart.MIDDLE) {
			worldIn.removeBlock(pos.up(), false);
			worldIn.removeBlock(pos.down(), false);
		} else if (state.get(PART) == LampPart.TOP) {
			worldIn.removeBlock(pos.down(), false);
			worldIn.removeBlock(pos.down(2), false);
		}
	}

	public static enum LampPart implements IStringSerializable {
		TOP("top"),
		MIDDLE("middle"),
		BOTTOM("bottom");

		private final String name;

		private LampPart(String name) {
			this.name = name;
		}

		public String toString() {
			return this.name;
		}

		public String getString() {
			return this.name;
		}
	}
}

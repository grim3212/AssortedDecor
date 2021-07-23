package com.grim3212.assorted.decor.common.block.colorizer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ColorizerLampPost extends ColorizerBlock implements SimpleWaterloggedBlock {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final EnumProperty<LampPart> PART = EnumProperty.create("part", LampPart.class);

	private static final VoxelShape TOP = Block.box(2F, 0.0F, 2F, 14F, 10.96F, 14F);
	private static final VoxelShape MIDDLE = Block.box(6F, 0.0F, 6F, 10F, 16F, 10F);
	private static final VoxelShape BOTTOM = Block.box(6F, 0.0F, 6F, 10F, 16F, 10F);

	public ColorizerLampPost() {
		this.registerDefaultState(this.stateDefinition.any().setValue(PART, LampPart.BOTTOM).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (state.getValue(PART) == LampPart.TOP) {
			return TOP;
		} else if (state.getValue(PART) == LampPart.MIDDLE) {
			return MIDDLE;
		} else {
			return BOTTOM;
		}
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		return state.getValue(PART) == LampPart.TOP ? 15 : super.getLightEmission(state, world, pos);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos blockpos = context.getClickedPos();
		FluidState fluidstate = context.getLevel().getFluidState(blockpos);
		return this.defaultBlockState().setValue(PART, LampPart.BOTTOM).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(PART, WATERLOGGED);
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.getValue(WATERLOGGED)) {
			worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}

		return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		worldIn.setBlock(pos.above(), state.setValue(PART, LampPart.MIDDLE), 3);
		worldIn.setBlock(pos.above(2), state.setValue(PART, LampPart.TOP), 3);
	}

	@Override
	public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
		if (state.getValue(PART) == LampPart.BOTTOM) {
			worldIn.removeBlock(pos.above(), false);
			worldIn.removeBlock(pos.above(2), false);
		} else if (state.getValue(PART) == LampPart.MIDDLE) {
			worldIn.removeBlock(pos.above(), false);
			worldIn.removeBlock(pos.below(), false);
		} else if (state.getValue(PART) == LampPart.TOP) {
			worldIn.removeBlock(pos.below(), false);
			worldIn.removeBlock(pos.below(2), false);
		}

		super.destroy(worldIn, pos, state);
	}

	@Override
	public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
		super.playerWillDestroy(worldIn, pos, state, player);

		if (state.getValue(PART) == LampPart.BOTTOM) {
			worldIn.removeBlock(pos.above(), false);
			worldIn.removeBlock(pos.above(2), false);
		} else if (state.getValue(PART) == LampPart.MIDDLE) {
			worldIn.removeBlock(pos.above(), false);
			worldIn.removeBlock(pos.below(), false);
		} else if (state.getValue(PART) == LampPart.TOP) {
			worldIn.removeBlock(pos.below(), false);
			worldIn.removeBlock(pos.below(2), false);
		}
	}

	@Override
	public boolean clearColorizer(Level worldIn, BlockPos pos, Player player, InteractionHand hand) {
		if (super.clearColorizer(worldIn, pos, player, hand)) {
			BlockState state = worldIn.getBlockState(pos);

			if (state.getValue(PART) == LampPart.BOTTOM) {
				return super.clearColorizer(worldIn, pos.above(), player, hand) && super.clearColorizer(worldIn, pos.above(2), player, hand);
			} else if (state.getValue(PART) == LampPart.MIDDLE) {
				return super.clearColorizer(worldIn, pos.below(), player, hand) && super.clearColorizer(worldIn, pos.above(), player, hand);
			} else if (state.getValue(PART) == LampPart.TOP) {
				return super.clearColorizer(worldIn, pos.below(), player, hand) && super.clearColorizer(worldIn, pos.below(2), player, hand);
			}
		}

		return false;
	}

	@Override
	public boolean setColorizer(Level worldIn, BlockPos pos, BlockState toSetState, Player player, InteractionHand hand, boolean consumeItem) {
		if (super.setColorizer(worldIn, pos, toSetState, player, hand, consumeItem)) {
			BlockState state = worldIn.getBlockState(pos);

			if (state.getValue(PART) == LampPart.BOTTOM) {
				return super.setColorizer(worldIn, pos.above(), toSetState, player, hand, consumeItem) && super.setColorizer(worldIn, pos.above(2), toSetState, player, hand, consumeItem);
			} else if (state.getValue(PART) == LampPart.MIDDLE) {
				return super.setColorizer(worldIn, pos.below(), toSetState, player, hand, consumeItem) && super.setColorizer(worldIn, pos.above(), toSetState, player, hand, consumeItem);
			} else if (state.getValue(PART) == LampPart.TOP) {
				return super.setColorizer(worldIn, pos.below(), toSetState, player, hand, consumeItem) && super.setColorizer(worldIn, pos.below(2), toSetState, player, hand, consumeItem);
			}
		}

		return false;
	}

	public static enum LampPart implements StringRepresentable {
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

		public String getSerializedName() {
			return this.name;
		}
	}
}

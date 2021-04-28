package com.grim3212.assorted.decor.common.block.colorizer;

import javax.annotation.Nullable;

import com.grim3212.assorted.decor.common.util.VerticalSlabType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class ColorizerVerticalSlabBlock extends ColorizerBlock implements IWaterLoggable {

	public static final EnumProperty<VerticalSlabType> TYPE = EnumProperty.create("type", VerticalSlabType.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape NORTH_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
	protected static final VoxelShape SOUTH_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);
	protected static final VoxelShape EAST_SHAPE = Block.makeCuboidShape(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

	public ColorizerVerticalSlabBlock() {
		super(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5f, 12.0f).sound(SoundType.STONE).variableOpacity().notSolid());
		this.setDefaultState(this.getDefaultState().with(TYPE, VerticalSlabType.NORTH).with(WATERLOGGED, false));
	}

	@Override
	public boolean isTransparent(BlockState state) {
		return state.get(TYPE) != VerticalSlabType.DOUBLE;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(TYPE, WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		VerticalSlabType slabtype = state.get(TYPE);
		switch (slabtype) {
		case DOUBLE:
			return VoxelShapes.fullCube();
		case NORTH:
			return NORTH_SHAPE;
		case EAST:
			return EAST_SHAPE;
		case WEST:
			return WEST_SHAPE;
		default:
			return SOUTH_SHAPE;
		}
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockPos blockpos = context.getPos();
		BlockState blockstate = context.getWorld().getBlockState(blockpos);
		if (blockstate.matchesBlock(this)) {
			return blockstate.with(TYPE, VerticalSlabType.DOUBLE).with(WATERLOGGED, false);
		} else {
			FluidState fluidstate = context.getWorld().getFluidState(blockpos);
			BlockState blockstate1 = this.getDefaultState().with(TYPE, VerticalSlabType.NORTH).with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
			Direction blockFace = context.getFace();
			Direction direction = context.getPlacementHorizontalFacing();

			if (blockFace != Direction.DOWN && blockFace != Direction.UP) {
				return blockstate1.with(TYPE, VerticalSlabType.fromDirection(direction));
			} else {
				if (direction == Direction.NORTH || direction == Direction.SOUTH) {
					return (context.getHitVec().z - (double) blockpos.getZ() > 0.5D) ? blockstate1.with(TYPE, VerticalSlabType.SOUTH) : blockstate1;
				} else if (direction == Direction.WEST || direction == Direction.EAST) {
					return (context.getHitVec().x - (double) blockpos.getX() > 0.5D) ? blockstate1.with(TYPE, VerticalSlabType.EAST) : blockstate1.with(TYPE, VerticalSlabType.WEST);
				}
			}
			return blockstate1;
		}
	}

	@Override
	public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
		ItemStack itemstack = useContext.getItem();
		VerticalSlabType slabtype = state.get(TYPE);
		if (slabtype != VerticalSlabType.DOUBLE && itemstack.getItem() == this.asItem()) {
			if (useContext.replacingClickedOnBlock()) {
				return useContext.getFace() == slabtype.toDirection().getOpposite();
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
		return state.get(TYPE) != VerticalSlabType.DOUBLE ? IWaterLoggable.super.receiveFluid(worldIn, pos, state, fluidStateIn) : false;
	}

	@Override
	public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
		return state.get(TYPE) != VerticalSlabType.DOUBLE ? IWaterLoggable.super.canContainFluid(worldIn, pos, state, fluidIn) : false;
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}

		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		switch (type) {
		case LAND:
			return false;
		case WATER:
			return worldIn.getFluidState(pos).isTagged(FluidTags.WATER);
		case AIR:
			return false;
		default:
			return false;
		}
	}

}

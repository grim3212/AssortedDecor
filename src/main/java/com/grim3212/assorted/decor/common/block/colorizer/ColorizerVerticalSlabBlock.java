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
	protected static final VoxelShape NORTH_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
	protected static final VoxelShape SOUTH_SHAPE = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);
	protected static final VoxelShape EAST_SHAPE = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

	public ColorizerVerticalSlabBlock() {
		super(Block.Properties.of(Material.STONE).strength(1.5f, 12.0f).sound(SoundType.STONE).dynamicShape().noOcclusion());
		this.registerDefaultState(this.defaultBlockState().setValue(TYPE, VerticalSlabType.NORTH).setValue(WATERLOGGED, false));
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return state.getValue(TYPE) != VerticalSlabType.DOUBLE;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(TYPE, WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		VerticalSlabType slabtype = state.getValue(TYPE);
		switch (slabtype) {
		case DOUBLE:
			return VoxelShapes.block();
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
		BlockPos blockpos = context.getClickedPos();
		BlockState blockstate = context.getLevel().getBlockState(blockpos);
		if (blockstate.is(this)) {
			return blockstate.setValue(TYPE, VerticalSlabType.DOUBLE).setValue(WATERLOGGED, false);
		} else {
			FluidState fluidstate = context.getLevel().getFluidState(blockpos);
			BlockState blockstate1 = this.defaultBlockState().setValue(TYPE, VerticalSlabType.NORTH).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
			Direction blockFace = context.getClickedFace();
			Direction direction = context.getHorizontalDirection();

			if (blockFace != Direction.DOWN && blockFace != Direction.UP) {
				return blockstate1.setValue(TYPE, VerticalSlabType.fromDirection(direction));
			} else {
				if (direction == Direction.NORTH || direction == Direction.SOUTH) {
					return (context.getClickLocation().z - (double) blockpos.getZ() > 0.5D) ? blockstate1.setValue(TYPE, VerticalSlabType.SOUTH) : blockstate1;
				} else if (direction == Direction.WEST || direction == Direction.EAST) {
					return (context.getClickLocation().x - (double) blockpos.getX() > 0.5D) ? blockstate1.setValue(TYPE, VerticalSlabType.EAST) : blockstate1.setValue(TYPE, VerticalSlabType.WEST);
				}
			}
			return blockstate1;
		}
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockItemUseContext useContext) {
		ItemStack itemstack = useContext.getItemInHand();
		VerticalSlabType slabtype = state.getValue(TYPE);
		if (slabtype != VerticalSlabType.DOUBLE && itemstack.getItem() == this.asItem()) {
			if (useContext.replacingClickedOnBlock()) {
				return useContext.getClickedFace() == slabtype.toDirection().getOpposite();
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean placeLiquid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
		return state.getValue(TYPE) != VerticalSlabType.DOUBLE ? IWaterLoggable.super.placeLiquid(worldIn, pos, state, fluidStateIn) : false;
	}

	@Override
	public boolean canPlaceLiquid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
		return state.getValue(TYPE) != VerticalSlabType.DOUBLE ? IWaterLoggable.super.canPlaceLiquid(worldIn, pos, state, fluidIn) : false;
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.getValue(WATERLOGGED)) {
			worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}

		return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		switch (type) {
		case LAND:
			return false;
		case WATER:
			return worldIn.getFluidState(pos).is(FluidTags.WATER);
		case AIR:
			return false;
		default:
			return false;
		}
	}

}

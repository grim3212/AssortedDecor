package com.grim3212.assorted.decor.common.block;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class NeonSignWallBlock extends NeonSignBlock {

	private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(0.0D, 4.5D, 14.0D, 16.0D, 12.5D, 16.0D), Direction.SOUTH, Block.box(0.0D, 4.5D, 0.0D, 16.0D, 12.5D, 2.0D), Direction.EAST, Block.box(0.0D, 4.5D, 0.0D, 2.0D, 12.5D, 16.0D), Direction.WEST, Block.box(14.0D, 4.5D, 0.0D, 16.0D, 12.5D, 16.0D)));

	public NeonSignWallBlock() {
		super(Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1f).noCollission().dropsLike(DecorBlocks.NEON_SIGN.get()));
		registerDefaultState(this.stateDefinition.any().setValue(WallSignBlock.FACING, Direction.NORTH).setValue(WallSignBlock.WATERLOGGED, false));
	}

	@Override
	public String getDescriptionId() {
		return this.asItem().getDescriptionId();
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(WallSignBlock.FACING, WallSignBlock.WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext ctx) {
		return SHAPES.get(state.getValue(WallSignBlock.FACING));
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.relative(state.getValue(WallSignBlock.FACING).getOpposite())).getMaterial().isSolid();
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState iblockstate = this.defaultBlockState();
		FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
		IWorldReader iworldreaderbase = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		Direction[] aenumfacing = context.getNearestLookingDirections();

		for (Direction enumfacing : aenumfacing) {
			if (enumfacing.getAxis().isHorizontal()) {
				Direction enumfacing1 = enumfacing.getOpposite();
				iblockstate = iblockstate.setValue(WallSignBlock.FACING, enumfacing1);
				if (iblockstate.canSurvive(iworldreaderbase, blockpos)) {
					return iblockstate.setValue(WallSignBlock.WATERLOGGED, ifluidstate.getType() == Fluids.WATER);
				}
			}
		}

		return null;
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		return facing.getOpposite() == stateIn.getValue(WallSignBlock.FACING) && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(WallSignBlock.FACING, rot.rotate(state.getValue(WallSignBlock.FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(WallSignBlock.FACING)));
	}
}

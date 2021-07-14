package com.grim3212.assorted.decor.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class NeonSignStandingBlock extends NeonSignBlock {

	public NeonSignStandingBlock() {
		super(Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).noCollission().strength(1f));
		this.registerDefaultState(this.stateDefinition.any().setValue(StandingSignBlock.ROTATION, 0).setValue(StandingSignBlock.WATERLOGGED, false));
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.below()).getMaterial().isSolid();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState().setValue(StandingSignBlock.ROTATION, MathHelper.floor((double) ((180.0F + context.getRotation()) * 16.0F / 360.0F) + 0.5D) & 15).setValue(StandingSignBlock.WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		return facing == Direction.DOWN && !this.canSurvive(stateIn, worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(StandingSignBlock.ROTATION, rot.rotate(state.getValue(StandingSignBlock.ROTATION), 16));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.setValue(StandingSignBlock.ROTATION, mirrorIn.mirror(state.getValue(StandingSignBlock.ROTATION), 16));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(StandingSignBlock.ROTATION, StandingSignBlock.WATERLOGGED);
	}

}

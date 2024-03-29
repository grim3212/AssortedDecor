package com.grim3212.assorted.decor.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;

public class NeonSignStandingBlock extends NeonSignBlock {

    public NeonSignStandingBlock() {
        super(Block.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).strength(10f).noCollission());
        this.registerDefaultState(this.stateDefinition.any().setValue(StandingSignBlock.ROTATION, 0).setValue(StandingSignBlock.WATERLOGGED, false));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.below()).isSolid();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(StandingSignBlock.ROTATION, Mth.floor((double) ((180.0F + context.getRotation()) * 16.0F / 360.0F) + 0.5D) & 15).setValue(StandingSignBlock.WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(StandingSignBlock.ROTATION, StandingSignBlock.WATERLOGGED);
    }

}

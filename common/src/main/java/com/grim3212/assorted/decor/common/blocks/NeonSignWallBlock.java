package com.grim3212.assorted.decor.common.blocks;

import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.util.RandomSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class NeonSignWallBlock extends NeonSignBlock {

    private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(0.0D, 4.5D, 14.0D, 16.0D, 12.5D, 16.0D), Direction.SOUTH, Block.box(0.0D, 4.5D, 0.0D, 16.0D, 12.5D, 2.0D), Direction.EAST, Block.box(0.0D, 4.5D, 0.0D, 2.0D, 12.5D, 16.0D), Direction.WEST, Block.box(14.0D, 4.5D, 0.0D, 16.0D, 12.5D, 16.0D)));

    public NeonSignWallBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(WallSignBlock.FACING, Direction.NORTH).setValue(WallSignBlock.WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(WallSignBlock.FACING, WallSignBlock.WATERLOGGED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext ctx) {
        return SHAPES.get(state.getValue(WallSignBlock.FACING));
    }

    // See NeonSignStandingBlock: isSolid() is deprecated with no replacement and vanilla's WallSignBlock
    // still uses it.
    @SuppressWarnings("deprecation")
    @Override
    protected boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.relative(state.getValue(WallSignBlock.FACING).getOpposite())).isSolid();
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState iblockstate = this.defaultBlockState();
        FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
        LevelReader iworldreaderbase = context.getLevel();
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
    protected BlockState updateShape(BlockState stateIn, LevelReader worldIn, ScheduledTickAccess ticks, BlockPos currentPos,
            Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
        return facing.getOpposite() == stateIn.getValue(WallSignBlock.FACING) && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, worldIn, ticks, currentPos, facing, facingPos, facingState, random);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(WallSignBlock.FACING, rot.rotate(state.getValue(WallSignBlock.FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(WallSignBlock.FACING)));
    }
}

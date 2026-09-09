package com.grim3212.assorted.decor.common.blocks;

import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerStoolBlock;
import com.grim3212.assorted.lib.core.block.IPlantSustainable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PlanterPotBlock extends Block implements IPlantSustainable {

    public static final IntegerProperty TOP = IntegerProperty.create("top", 0, 6);
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    public PlanterPotBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(TOP, 0).setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(TOP, DOWN);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, Block plant) {
        if (plant instanceof MushroomBlock) {
            return true;
        }

        BlockState planterState = world.getBlockState(pos);
        int top = planterState.getValue(TOP);
        switch (top) {
            case 0:
                if (plant instanceof FlowerBlock || plant instanceof SaplingBlock || plant instanceof BambooStalkBlock) {
                    return true;
                }
                // Plants that take up the full block space aren't allowed with a stool
                if (this.isStool(world, pos)) {
                    return false;
                }

                if (plant instanceof SugarCaneBlock) {
                    boolean hasWater = (world.getBlockState(pos.east()).getFluidState().is(Fluids.WATER) || world.getBlockState(pos.west()).getFluidState().is(Fluids.WATER) || world.getBlockState(pos.north()).getFluidState().is(Fluids.WATER) || world.getBlockState(pos.south()).getFluidState().is(Fluids.WATER));
                    return hasWater;
                }
                break;
            case 1:
                if (plant instanceof DryVegetationBlock || plant instanceof CactusBlock || plant instanceof BambooStalkBlock) {
                    return true;
                }
                // Plants that take up the full block space aren't allowed with a stool
                if (this.isStool(world, pos)) {
                    return false;
                }
                if (plant instanceof SugarCaneBlock) {
                    boolean hasWater = (world.getBlockState(pos.east()).getFluidState().is(Fluids.WATER) || world.getBlockState(pos.west()).getFluidState().is(Fluids.WATER) || world.getBlockState(pos.north()).getFluidState().is(Fluids.WATER) || world.getBlockState(pos.south()).getFluidState().is(Fluids.WATER));
                    return hasWater;
                }
                break;
            case 2:
                if (plant instanceof BambooStalkBlock) {
                    return true;
                }
                break;
            case 4:
                if ((plant instanceof CropBlock || plant instanceof StemBlock) && !this.isStool(world, pos)) {
                    return true;
                }
                break;
            case 6:
                if (plant instanceof NetherWartBlock && !this.isStool(world, pos)) {
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        worldIn.updateNeighborsAt(pos, this);
    }

    @Override
    protected void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, @Nullable Orientation orientation, boolean movedByPiston) {
        worldIn.scheduleTick(pos, this, 10);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext ctx) {
        if (isStool(worldIn, pos))
            return ColorizerStoolBlock.POT_STOOL;
        return Shapes.block();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.getMainHandItem().isEmpty()) {
            int top = worldIn.getBlockState(pos).getValue(TOP);
            if (top == 6) {
                top = 0;
            } else {
                top++;
            }
            worldIn.setBlock(pos, state.setValue(TOP, top), 2);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    private boolean isStool(BlockGetter worldIn, BlockPos pos) {
        BlockState stoolState = worldIn.getBlockState(pos.below());
        if (stoolState.getBlock() == DecorBlocks.COLORIZER_STOOL.get()) {
            return stoolState.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.FLOOR;
        }

        return false;
    }

    @Override
    protected BlockState updateShape(BlockState stateIn, LevelReader worldIn, ScheduledTickAccess ticks, BlockPos currentPos,
            Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
        return stateIn.setValue(DOWN, this.isStool(worldIn, currentPos));
    }
}

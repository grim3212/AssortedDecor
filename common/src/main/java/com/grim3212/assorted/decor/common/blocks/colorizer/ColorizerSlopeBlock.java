package com.grim3212.assorted.decor.common.blocks.colorizer;


import com.grim3212.assorted.decor.api.colorizer.SlopeType;
import com.grim3212.assorted.decor.api.util.DecorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ColorizerSlopeBlock extends ColorizerRotateBlock {

    private final SlopeType type;

    public ColorizerSlopeBlock(SlopeType type) {
        this.type = type;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return DecorUtil.addAxisAlignedBoxes(state, worldIn, pos, context, this.type.getNumPieces());
    }

}

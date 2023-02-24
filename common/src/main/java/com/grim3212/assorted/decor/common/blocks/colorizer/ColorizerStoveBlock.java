package com.grim3212.assorted.decor.common.blocks.colorizer;

import com.grim3212.assorted.decor.api.util.DecorUtil;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ColorizerStoveBlock extends ColorizerFireplaceBaseBlock {

    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
        if (worldIn.getBlockState(pos).getValue(ACTIVE) && worldIn.getBlockState(pos.above()).getBlock() == DecorBlocks.COLORIZER_CHIMNEY.get()) {
            int smokeheight = 1;
            while (worldIn.getBlockState(pos.above(smokeheight)).getBlock() == DecorBlocks.COLORIZER_CHIMNEY.get()) {
                smokeheight++;
            }

            DecorUtil.produceSmoke(worldIn, pos.above(smokeheight), 0.5D, 0.0D, 0.5D, 1, true);
        }
    }
}

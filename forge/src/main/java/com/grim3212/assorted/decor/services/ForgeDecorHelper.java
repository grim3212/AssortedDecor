package com.grim3212.assorted.decor.services;

import com.grim3212.assorted.decor.common.block.blockentity.ForgeColorizerBlockEntity;
import com.grim3212.assorted.decor.common.blocks.blockentity.ColorizerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ForgeDecorHelper implements IDecorHelper {
    @Override
    public CageItemEntry[] getCageItems() {
        return new CageItemEntry[0];
    }

    @Override
    public ColorizerBlockEntity createColorizerBlockEntity(BlockPos pos, BlockState state) {
        return new ForgeColorizerBlockEntity(pos, state);
    }
}

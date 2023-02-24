package com.grim3212.assorted.decor.services;

import com.grim3212.assorted.decor.common.blocks.blockentity.ColorizerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public interface IDecorHelper {

    CageItemEntry[] getCageItems();

    record CageItemEntry(ResourceLocation itemId, String entityTag) {
    }

    ColorizerBlockEntity createColorizerBlockEntity(BlockPos pos, BlockState state);
}

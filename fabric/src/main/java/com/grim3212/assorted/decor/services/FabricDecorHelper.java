package com.grim3212.assorted.decor.services;

import com.grim3212.assorted.decor.common.block.blockentity.FabricColorizerBlockEntity;
import com.grim3212.assorted.decor.common.blocks.blockentity.ColorizerBlockEntity;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FabricDecorHelper implements IDecorHelper {
    @Override
    public CageItemEntry[] getCageItems() {
        if (FabricLoader.getInstance().getObjectShare().get("assorteddecor:cage_items") instanceof CageItemEntry[] cageEntries) {
            return cageEntries;
        }

        return new CageItemEntry[0];
    }

    @Override
    public ColorizerBlockEntity createColorizerBlockEntity(BlockPos pos, BlockState state) {
        return new FabricColorizerBlockEntity(pos, state);
    }
}

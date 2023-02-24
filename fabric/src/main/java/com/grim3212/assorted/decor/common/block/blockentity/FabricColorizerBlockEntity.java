package com.grim3212.assorted.decor.common.block.blockentity;

import com.grim3212.assorted.decor.common.blocks.blockentity.ColorizerBlockEntity;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FabricColorizerBlockEntity extends ColorizerBlockEntity implements RenderAttachmentBlockEntity {

    public FabricColorizerBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public Object getRenderAttachmentData() {
        return this.getStoredBlockState();
    }
}

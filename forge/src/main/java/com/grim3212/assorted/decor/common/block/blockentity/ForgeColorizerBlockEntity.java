package com.grim3212.assorted.decor.common.block.blockentity;

import com.grim3212.assorted.decor.common.blocks.blockentity.ColorizerBlockEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;

public class ForgeColorizerBlockEntity extends ColorizerBlockEntity {

    public static final ModelProperty<BlockState> BLOCK_STATE = new ModelProperty<BlockState>();

    public ForgeColorizerBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    @NotNull
    public ModelData getModelData() {
        return ModelData.builder().with(BLOCK_STATE, this.getStoredBlockState()).build();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        requestModelDataUpdate();
        if (level instanceof ClientLevel) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
        }
    }
}

package com.grim3212.assorted.decor.common.blocks.blockentity;

import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ColorizerBlockEntity extends BlockEntity {

    protected BlockState storedBlockState = Blocks.AIR.defaultBlockState();

    public ColorizerBlockEntity(BlockPos pos, BlockState state) {
        super(DecorBlockEntityTypes.COLORIZER.get(), pos, state);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.storedBlockState = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), nbt.getCompound("stored_state"));
    }

    @Override
    protected void saveAdditional(CompoundTag cmp) {
        super.saveAdditional(cmp);
        if (Services.PLATFORM.getRegistry(Registries.BLOCK).contains(this.storedBlockState.getBlock()))
            cmp.put("stored_state", NbtUtils.writeBlockState(this.storedBlockState));
        else
            cmp.put("stored_state", NbtUtils.writeBlockState(Blocks.AIR.defaultBlockState()));
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public BlockState getStoredBlockState() {
        return storedBlockState;
    }

    public void setStoredBlockState(BlockState blockState) {
        this.storedBlockState = blockState;

        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            level.getLightEngine().checkBlock(getBlockPos());
            if (!level.isClientSide) {
                level.blockUpdated(worldPosition, getBlockState().getBlock());
            }
        }

        this.setChanged();
    }

    public void setStoredBlockState(String registryName) {
        this.setStoredBlockState(Services.PLATFORM.getRegistry(Registries.BLOCK).getValue(new ResourceLocation(registryName)).orElseGet(() -> Blocks.AIR).defaultBlockState());
    }
}

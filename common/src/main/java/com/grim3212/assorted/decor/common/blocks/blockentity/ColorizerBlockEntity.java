package com.grim3212.assorted.decor.common.blocks.blockentity;

import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.DataComponentGetter;
import com.grim3212.assorted.decor.common.properties.DecorModelProperties;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.client.model.data.IModelDataBuilder;
import com.grim3212.assorted.lib.core.block.IBlockEntityWithModelData;
import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public class ColorizerBlockEntity extends BlockEntity implements IBlockEntityWithModelData {

    protected BlockState storedBlockState = Blocks.AIR.defaultBlockState();

    public ColorizerBlockEntity(BlockPos pos, BlockState state) {
        super(DecorBlockEntityTypes.COLORIZER.get(), pos, state);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.storedBlockState = input.read("stored_state", BlockState.CODEC).orElse(Blocks.AIR.defaultBlockState());
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (Services.PLATFORM.getRegistry(Registries.BLOCK).contains(this.storedBlockState.getBlock()))
            output.store("stored_state", BlockState.CODEC, this.storedBlockState);
        else
            output.store("stored_state", BlockState.CODEC, Blocks.AIR.defaultBlockState());
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /**
     * A colorizer placed from an item takes the block the item carries as {@code stored_state} in its
     * custom data. {@code BlockItem#place} hands the stack's components over here before
     * {@code setPlacedBy}, and reading {@code custom_data} marks it used, so it is not also kept on
     * the block entity.
     */
    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        CompoundTag data = components.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (data.contains("stored_state")) {
            this.setStoredBlockState(NbtUtils.readBlockState(BuiltInRegistries.BLOCK, data.getCompoundOrEmpty("stored_state")));
        }
    }

    public BlockState getStoredBlockState() {
        return storedBlockState;
    }

    public void setStoredBlockState(BlockState blockState) {
        this.storedBlockState = blockState;

        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            level.getLightEngine().checkBlock(getBlockPos());
            if (!level.isClientSide()) {
                level.updateNeighborsAt(worldPosition, getBlockState().getBlock(), null);
            } else {
                ClientServices.MODELS.requestModelDataRefresh(this);
            }
        }

        this.setChanged();
    }

    public void setStoredBlockState(String registryName) {
        this.setStoredBlockState(Services.PLATFORM.getRegistry(Registries.BLOCK).getValue(Identifier.parse(registryName)).orElseGet(() -> Blocks.AIR).defaultBlockState());
    }

    @Override
    public @NotNull IBlockModelData getBlockModelData() {
        return IModelDataBuilder.create().withInitial(DecorModelProperties.BLOCK_STATE, this.storedBlockState).build();
    }
}

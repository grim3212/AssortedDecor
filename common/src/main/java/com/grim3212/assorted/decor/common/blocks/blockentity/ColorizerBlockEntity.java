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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerFullCubeBlock;
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
     * Takes the {@code stored_state} from the placing item's custom data. Reading it marks it used,
     * so it is not also kept on the block entity.
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
            // Light dampening is baked into the block state, so a full cube carries its stored
            // block's in a property. Changing it is an ordinary block update, and vanilla does the
            // rest: relights, recomputes the sky column and sends the state to every client. Server
            // only; a client takes the state from that update.
            if (!level.isClientSide() && getBlockState().getBlock() instanceof ColorizerFullCubeBlock) {
                final BlockState lit = ColorizerFullCubeBlock.withStoredDampening(getBlockState(), blockState);
                if (lit != getBlockState()) {
                    level.setBlock(worldPosition, lit, Block.UPDATE_ALL);
                }
            }
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            // Emission is still read from the stored block per position, so the light has to be
            // asked to look again.
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

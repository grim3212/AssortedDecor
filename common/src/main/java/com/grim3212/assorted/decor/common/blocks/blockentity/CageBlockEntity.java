package com.grim3212.assorted.decor.common.blocks.blockentity;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.api.DecorTags;
import com.grim3212.assorted.decor.common.helpers.CageLogic;
import com.grim3212.assorted.decor.common.inventory.CageContainer;
import com.grim3212.assorted.lib.core.inventory.IInventoryBlockEntity;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import com.grim3212.assorted.lib.core.inventory.impl.ItemStackStorageHandler;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CageBlockEntity extends BlockEntity implements IInventoryBlockEntity, MenuProvider, Nameable {

    private Entity cachedEntity;
    private Component customName;

    private final CageLogic cageLogic = new CageLogic(this);

    protected IPlatformInventoryStorageHandler platformInventoryStorageHandler;
    private final ItemStackStorageHandler storageHandler;

    public CageBlockEntity(BlockPos pos, BlockState state) {
        this(DecorBlockEntityTypes.CAGE.get(), pos, state);
    }

    public CageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        this.storageHandler = new ItemStackStorageHandler(1) {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return isValidCage(stack) != null;
            }

            @Override
            public void onContentsChanged(int slot) {
                CageBlockEntity.this.setChanged();
            }

            @Override
            public boolean stillValid(Player player) {
                if (CageBlockEntity.this.level.getBlockEntity(CageBlockEntity.this.worldPosition) != CageBlockEntity.this) {
                    return false;
                } else {
                    return player.distanceToSqr((double) CageBlockEntity.this.worldPosition.getX() + 0.5D, (double) CageBlockEntity.this.worldPosition.getY() + 0.5D, (double) CageBlockEntity.this.worldPosition.getZ() + 0.5D) <= 64.0D;
                }
            }
        };
    }

    @Override
    public IPlatformInventoryStorageHandler getStorageHandler() {
        if (this.platformInventoryStorageHandler == null) {
            this.platformInventoryStorageHandler = this.createStorageHandler();
        }

        return this.platformInventoryStorageHandler;
    }

    public IPlatformInventoryStorageHandler createStorageHandler() {
        return Services.INVENTORY.createStorageInventoryHandler(this.storageHandler);
    }

    public ItemStackStorageHandler getItemStackStorageHandler() {
        return this.storageHandler;
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
        return new CageContainer(windowId, playerInventory, this.storageHandler);
    }

    public CageLogic getCageLogic() {
        return cageLogic;
    }

    public void clientTick() {
        this.cageLogic.clientTick();
    }

    public void serverTick() {
        this.cageLogic.serverTick();
    }

    public void setCustomName(Component name) {
        this.customName = name;
    }

    @Override
    public Component getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    public Component getCustomName() {
        return this.customName;
    }

    protected Component getDefaultName() {
        return Component.translatable(Constants.MOD_ID + ".container.cage");
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.platformInventoryStorageHandler != null) {
            this.platformInventoryStorageHandler.invalidate();
        }
        this.cachedEntity = null;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("Inventory"))
            this.storageHandler.deserializeNBT(nbt.getCompound("Inventory"));

        if (nbt.contains("CustomName", 8)) {
            this.customName = Component.Serializer.fromJson(nbt.getString("CustomName"));
        }

        this.cachedEntity = null;
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);

        compound.put("Inventory", this.storageHandler.serializeNBT());

        if (this.customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(this.customName));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        this.cachedEntity = null;
        super.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        this.getCachedEntity();
    }

    public Entity getCachedEntity() {
        if (this.cachedEntity == null) {
            ItemStack stack = this.storageHandler.getStackInSlot(0);
            if (!stack.isEmpty()) {
                String tag = isValidCage(stack);
                if (tag != null) {
                    this.storeEntity(stack, tag);
                }
            }
        }
        return this.cachedEntity;
    }

    private void storeEntity(ItemStack stack, String tag) {
        if (stack.getItem() instanceof SpawnEggItem spawnEgg) {
            Entity ent = spawnEgg.getType(stack.getTag()).create(this.level);
            if (ent != null) {
                this.cachedEntity = ent;
                return;
            }
        }

        Optional<Entity> loadEntity = EntityType.create(NBTHelper.getTag(stack, tag), this.level);
        if (loadEntity.isPresent()) {
            this.cachedEntity = loadEntity.get();
        }
    }

    public static String isValidCage(ItemStack stack) {
        if (stack.getItem() instanceof SpawnEggItem) {
            return "EntityTag";
        }

        if (!stack.is(DecorTags.Items.CAGE_SUPPORTED)) {
            return null;
        }

        if (NBTHelper.hasTag(stack, "StoredEntity")) {
            return "StoredEntity";
        }

        if (NBTHelper.hasTag(stack, "EntityTag")) {
            return "EntityTag";
        }

        return null;
    }
}

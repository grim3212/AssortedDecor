package com.grim3212.assorted.decor.common.blocks.blockentity;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.api.DecorTags;
import com.grim3212.assorted.decor.common.helpers.CageLogic;
import com.grim3212.assorted.decor.common.inventory.CageContainer;
import com.grim3212.assorted.lib.core.inventory.IInventoryBlockEntity;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import com.grim3212.assorted.lib.core.inventory.impl.ItemStackStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.slf4j.Logger;
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
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;


public class CageBlockEntity extends BlockEntity implements IInventoryBlockEntity, MenuProvider, Nameable {

    private static final Logger LOGGER = LogUtils.getLogger();

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
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.storageHandler.deserialize(input.childOrEmpty("Inventory"));
        this.customName = parseCustomNameSafe(input, "CustomName");

        this.cachedEntity = null;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        this.storageHandler.serialize(output.child("Inventory"));
        output.storeNullable("CustomName", ComponentSerialization.CODEC, this.customName);
    }

    /**
     * Drops the caged stack. It has to happen here: the block entity is already gone by
     * {@code affectNeighborsAfterRemoval}.
     */
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);

        if (this.level != null) {
            StorageUtil.dropContents(this.level, pos, this.storageHandler);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
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

    /**
     * Builds the caged mob. It is only ever drawn, never added to a level, so it has to be marked as
     * a display entity the way a spawner marks its own: {@link Level#getNextEntityId()} answers 0 on
     * the client, and {@link Entity#getId()} now throws on an id of 0 rather than returning it. The
     * renderer reaches that through {@code ItemModelResolver#updateForLiving}, which every living
     * entity's render state extraction calls for the head slot whether or not anything is worn.
     */
    private void storeEntity(ItemStack stack, String tag) {
        if (stack.getItem() instanceof SpawnEggItem) {
            // Spawn eggs carry their entity in the ENTITY_DATA component now, not in stack NBT
            EntityType<?> eggType = SpawnEggItem.getType(stack);
            if (eggType != null) {
                Entity ent = eggType.create(this.level, EntitySpawnReason.LOAD);
                if (ent != null) {
                    this.cachedEntity = BaseSpawner.SET_DISPLAY_ENTITY_ID.process(ent);
                    return;
                }
            }
        }

        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
            ValueInput storedEntity = TagValueInput.create(reporter, this.level.registryAccess(), NBTHelper.getTag(stack, tag));
            this.cachedEntity = EntityType.loadEntityRecursive(storedEntity, this.level, new EntitySpawnRequest(EntitySpawnReason.LOAD, false), BaseSpawner.SET_DISPLAY_ENTITY_ID);
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

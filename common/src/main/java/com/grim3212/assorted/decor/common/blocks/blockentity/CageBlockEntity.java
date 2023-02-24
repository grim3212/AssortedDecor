package com.grim3212.assorted.decor.common.blocks.blockentity;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.DecorServices;
import com.grim3212.assorted.decor.common.helpers.CageLogic;
import com.grim3212.assorted.decor.common.inventory.CageContainer;
import com.grim3212.assorted.decor.services.IDecorHelper;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.WorldlyContainer;
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

import java.util.Optional;

public class CageBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider, Nameable {

    private Entity cachedEntity;
    private Component customName;
    // Only 1 slot but a list makes it easier to use some of the default methods
    private NonNullList<ItemStack> storedItems = NonNullList.withSize(1, ItemStack.EMPTY);

    private final CageLogic cageLogic = new CageLogic(this);

    public CageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public CageBlockEntity(BlockPos pos, BlockState state) {
        super(DecorBlockEntityTypes.CAGE.get(), pos, state);
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
        return new CageContainer(windowId, playerInventory, this);
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
    public void clearContent() {
        this.storedItems.clear();
        this.cachedEntity = null;
        this.markUpdated();
    }

    @Override
    public int getContainerSize() {
        return this.storedItems.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.storedItems) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.storedItems.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        this.cachedEntity = null;
        this.markUpdated();
        return ContainerHelper.removeItem(this.storedItems, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        this.cachedEntity = null;
        this.markUpdated();
        return ContainerHelper.takeItem(this.storedItems, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.storedItems.set(index, stack);
        this.cachedEntity = null;
        this.markUpdated();

        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    private static final int[] SLOT = new int[]{0};

    @Override
    public int[] getSlotsForFace(Direction dir) {
        return SLOT;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction dir) {
        return isValidCage(stack) != null;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.storedItems = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.storedItems);

        if (nbt.contains("CustomName", 8)) {
            this.customName = Component.Serializer.fromJson(nbt.getString("CustomName"));
        }

        this.cachedEntity = null;
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);

        ContainerHelper.saveAllItems(compound, this.storedItems);

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

    private void markUpdated() {
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        this.getCachedEntity();
    }

    public Entity getCachedEntity() {
        if (this.cachedEntity == null) {
            ItemStack stack = this.storedItems.get(0);
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

        for (IDecorHelper.CageItemEntry cageEntry : DecorServices.DECOR.getCageItems()) {
            // Do the items match
            if (Services.PLATFORM.getRegistry(Registries.ITEM).getRegistryName(stack.getItem()).equals(cageEntry.itemId())) {
                // Does the stack contain the correct Compound tag
                return NBTHelper.hasTag(stack, cageEntry.entityTag()) ? cageEntry.entityTag() : null;
            }
        }
        return null;
    }
}

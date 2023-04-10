package com.grim3212.assorted.decor.common.inventory;

import com.grim3212.assorted.decor.common.blocks.blockentity.CageBlockEntity;
import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.slot.SlotStorageHandler;
import net.minecraft.world.item.ItemStack;

public class CageSlot extends SlotStorageHandler {

    public CageSlot(IItemStorageHandler forgeInventory, int id, int x, int y) {
        super(forgeInventory, id, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return CageBlockEntity.isValidCage(stack) != null;
    }

}

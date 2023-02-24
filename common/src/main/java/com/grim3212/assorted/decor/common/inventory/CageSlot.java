package com.grim3212.assorted.decor.common.inventory;

import com.grim3212.assorted.decor.common.blocks.blockentity.CageBlockEntity;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CageSlot extends Slot {

	public CageSlot(Container forgeInventory, int id, int x, int y) {
		super(forgeInventory, id, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return CageBlockEntity.isValidCage(stack) != null;
	}

}

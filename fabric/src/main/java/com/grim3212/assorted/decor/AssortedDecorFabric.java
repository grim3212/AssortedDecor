package com.grim3212.assorted.decor;

import com.grim3212.assorted.decor.common.blocks.blockentity.DecorBlockEntityTypes;
import com.grim3212.assorted.lib.core.inventory.IInventoryBlockEntity;
import com.grim3212.assorted.lib.inventory.FabricPlatformInventoryStorageHandlerUnsided;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;

public class AssortedDecorFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        DecorCommonMod.init();

        ItemStorage.SIDED.registerForBlockEntities((be, direction) ->
                {
                    if (be instanceof IInventoryBlockEntity inv)
                        return ((FabricPlatformInventoryStorageHandlerUnsided) inv.getStorageHandler()).getFabricInventory();
                    return null;
                },
                DecorBlockEntityTypes.CAGE.get()
        );
    }
}

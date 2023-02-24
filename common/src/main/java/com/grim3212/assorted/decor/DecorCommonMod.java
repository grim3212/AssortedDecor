package com.grim3212.assorted.decor;

import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.blockentity.DecorBlockEntityTypes;
import com.grim3212.assorted.decor.common.crafting.DecorConditions;
import com.grim3212.assorted.decor.common.entity.DecorEntityTypes;
import com.grim3212.assorted.decor.common.events.DecorEvents;
import com.grim3212.assorted.decor.common.inventory.DecorContainerTypes;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.decor.common.network.DecorPackets;
import com.grim3212.assorted.lib.platform.Services;

public class DecorCommonMod {

    public static void init() {
        DecorBlocks.init();
        DecorItems.init();
        DecorBlockEntityTypes.init();
        DecorEntityTypes.init();
        DecorContainerTypes.init();
        DecorEvents.init();
        DecorPackets.init();
        DecorConditions.init();

        Services.PLATFORM.setupCommonConfig(Constants.MOD_ID, DecorConfig.Common.COMMON_CONFIG);
        Services.PLATFORM.setupClientConfig(Constants.MOD_ID, DecorConfig.Client.CLIENT_CONFIG);
    }
}

package com.grim3212.assorted.decor;

import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.blockentity.DecorBlockEntityTypes;
import com.grim3212.assorted.decor.common.crafting.DecorConditions;
import com.grim3212.assorted.decor.common.entity.DecorEntityTypes;
import com.grim3212.assorted.decor.common.events.DecorEvents;
import com.grim3212.assorted.decor.common.helpers.DecorCreativeItems;
import com.grim3212.assorted.decor.common.inventory.DecorContainerTypes;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.decor.common.network.DecorPackets;
import com.grim3212.assorted.decor.config.DecorCommonConfig;

public class DecorCommonMod {

    public static final DecorCommonConfig COMMON_CONFIG = new DecorCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        DecorBlocks.init();
        DecorItems.init();
        DecorBlockEntityTypes.init();
        DecorEntityTypes.init();
        DecorContainerTypes.init();
        DecorEvents.init();
        DecorPackets.init();
        DecorConditions.init();
        DecorCreativeItems.init();
    }
}

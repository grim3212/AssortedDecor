package com.grim3212.assorted.decor;

import com.grim3212.assorted.decor.client.DecorClientProxy;
import com.grim3212.assorted.decor.common.blocks.blockentity.NeonSignBlockEntity;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.BlockPos;

public interface DecorProxy {

    DecorProxy INSTANCE = make();

    private static DecorProxy make() {
        if (Services.PLATFORM.isPhysicalClient()) {
            return new DecorClientProxy();
        } else {
            return new DecorProxy() {
            };
        }
    }

    default void openNeonSignScreen(NeonSignBlockEntity sign) {

    }

    default void handleOpenNeonSign(BlockPos pos) {

    }
}

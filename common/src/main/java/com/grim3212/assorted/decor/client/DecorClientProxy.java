package com.grim3212.assorted.decor.client;

import com.grim3212.assorted.decor.DecorProxy;
import com.grim3212.assorted.decor.client.screen.NeonSignScreen;
import com.grim3212.assorted.decor.common.blocks.blockentity.NeonSignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DecorClientProxy implements DecorProxy {

    @Override
    public void openNeonSignScreen(NeonSignBlockEntity sign) {
        Minecraft.getInstance().setScreen(new NeonSignScreen(sign));
    }

    @Override
    public void handleOpenNeonSign(BlockPos pos) {
        BlockEntity tileentity = Minecraft.getInstance().player.getCommandSenderWorld().getBlockEntity(pos);

        // Make sure TileEntity exists
        if (!(tileentity instanceof NeonSignBlockEntity)) {
            tileentity = new NeonSignBlockEntity(pos, tileentity.getBlockState());
            tileentity.setLevel(Minecraft.getInstance().player.getCommandSenderWorld());
        }

        openNeonSignScreen((NeonSignBlockEntity) tileentity);
    }
}

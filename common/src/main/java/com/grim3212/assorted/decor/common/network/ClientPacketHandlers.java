package com.grim3212.assorted.decor.common.network;

import com.grim3212.assorted.decor.client.screen.NeonSignScreen;
import com.grim3212.assorted.decor.common.blocks.blockentity.NeonSignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ClientPacketHandlers {
    public static void openNeonSignScreen(BlockPos pos) {
        BlockEntity tileentity = Minecraft.getInstance().player.getCommandSenderWorld().getBlockEntity(pos);

        // Make sure TileEntity exists
        if (!(tileentity instanceof NeonSignBlockEntity)) {
            tileentity = new NeonSignBlockEntity(pos, tileentity.getBlockState());
            tileentity.setLevel(Minecraft.getInstance().player.getCommandSenderWorld());
        }

        Minecraft.getInstance().setScreen(new NeonSignScreen((NeonSignBlockEntity) tileentity));
    }
}

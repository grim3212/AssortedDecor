package com.grim3212.assorted.decor.common.network;

import com.grim3212.assorted.lib.dist.Dist;
import com.grim3212.assorted.lib.dist.DistExecutor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class NeonOpenPacket {

    private BlockPos pos;

    public NeonOpenPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static NeonOpenPacket decode(FriendlyByteBuf buf) {
        return new NeonOpenPacket(buf.readBlockPos());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    public static void handle(NeonOpenPacket packet, Player player) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.openNeonSignScreen(packet.pos));
    }

}

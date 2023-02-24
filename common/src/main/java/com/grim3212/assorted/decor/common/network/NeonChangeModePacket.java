package com.grim3212.assorted.decor.common.network;

import com.grim3212.assorted.decor.common.blocks.blockentity.NeonSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public class NeonChangeModePacket {

    private int mode;
    private BlockPos pos;

    public NeonChangeModePacket(int mode, BlockPos pos) {
        this.mode = mode;
        this.pos = pos;
    }

    public static NeonChangeModePacket decode(FriendlyByteBuf buf) {
        return new NeonChangeModePacket(buf.readInt(), buf.readBlockPos());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.mode);
        buf.writeBlockPos(this.pos);
    }

    public static void handle(NeonChangeModePacket packet, Player player) {
        BlockEntity te = player.getCommandSenderWorld().getBlockEntity(packet.pos);
        if (te instanceof NeonSignBlockEntity) {
            ((NeonSignBlockEntity) te).mode = packet.mode;
        }
    }
}

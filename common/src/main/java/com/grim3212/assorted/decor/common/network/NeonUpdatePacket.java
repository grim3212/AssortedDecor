package com.grim3212.assorted.decor.common.network;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.common.blocks.blockentity.NeonSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NeonUpdatePacket {

    private BlockPos pos;
    private MutableComponent[] lines;

    public NeonUpdatePacket(BlockPos pos, MutableComponent[] linesIn) {
        this.pos = pos;

        // We want to save formatting codes
        this.lines = new MutableComponent[]{linesIn[0], linesIn[1], linesIn[2], linesIn[3]};
    }

    public static NeonUpdatePacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();

        MutableComponent[] lines = new MutableComponent[4];
        for (int i = 0; i < 4; i++) {
            lines[i] = (MutableComponent) buf.readComponent();
        }

        return new NeonUpdatePacket(pos, lines);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);

        for (int i = 0; i < 4; i++) {
            buf.writeComponent(this.lines[i]);
        }
    }

    public static void handle(NeonUpdatePacket packet, Player player) {
        BlockState state = player.level.getBlockState(packet.pos);
        BlockEntity te = player.level.getBlockEntity(packet.pos);

        if (te instanceof NeonSignBlockEntity) {
            NeonSignBlockEntity neonSign = (NeonSignBlockEntity) te;
            if (!neonSign.getOwner().getUUID().equals(player.getUUID())) {
                Constants.LOG.warn("Player " + player.getName().getString() + " just tried to change a neon sign they don't own");
                return;
            }

            for (int i = 0; i < packet.lines.length; ++i) {
                // Again do not strip away formatting codes
                neonSign.signText[i] = packet.lines[i];
            }

            neonSign.setChanged();
            player.level.sendBlockUpdated(packet.pos, state, state, 3);
        }
    }
}

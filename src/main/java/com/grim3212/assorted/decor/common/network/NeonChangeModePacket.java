package com.grim3212.assorted.decor.common.network;

import java.util.function.Supplier;

import com.grim3212.assorted.decor.common.block.blockentity.NeonSignBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

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

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
			ctx.get().enqueueWork(() -> {
				BlockEntity te = ctx.get().getSender().getCommandSenderWorld().getBlockEntity(this.pos);
				if (te instanceof NeonSignBlockEntity) {
					((NeonSignBlockEntity) te).mode = this.mode;
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}
}

package com.grim3212.assorted.decor.common.network;

import java.util.function.Supplier;

import com.grim3212.assorted.decor.AssortedDecor;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

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

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
			ctx.get().enqueueWork(() -> {
				AssortedDecor.proxy.handleOpenNeonSign(this.pos);
			});
			ctx.get().setPacketHandled(true);
		}
	}

}

package com.grim3212.assorted.decor.common.network;

import java.util.function.Supplier;

import com.grim3212.assorted.decor.AssortedDecor;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class NeonOpenPacket {

	private BlockPos pos;

	public NeonOpenPacket(BlockPos pos) {
		this.pos = pos;
	}

	public static NeonOpenPacket decode(PacketBuffer buf) {
		return new NeonOpenPacket(buf.readBlockPos());
	}

	public void encode(PacketBuffer buf) {
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

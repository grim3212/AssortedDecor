package com.grim3212.assorted.decor.common.network;

import java.util.function.Supplier;

import com.grim3212.assorted.decor.common.block.tileentity.NeonSignTileEntity;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class NeonChangeModePacket {

	private int mode;
	private BlockPos pos;

	public NeonChangeModePacket(int mode, BlockPos pos) {
		this.mode = mode;
		this.pos = pos;
	}

	public static NeonChangeModePacket decode(PacketBuffer buf) {
		return new NeonChangeModePacket(buf.readInt(), buf.readBlockPos());
	}

	public void encode(PacketBuffer buf) {
		buf.writeInt(this.mode);
		buf.writeBlockPos(this.pos);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
			ctx.get().enqueueWork(() -> {
				TileEntity te = ctx.get().getSender().getEntityWorld().getTileEntity(this.pos);
				if (te instanceof NeonSignTileEntity) {
					((NeonSignTileEntity) te).mode = this.mode;
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}
}

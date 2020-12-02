package com.grim3212.assorted.decor.common.network;

import java.util.function.Supplier;

import com.grim3212.assorted.decor.client.screen.NeonSignScreen;
import com.grim3212.assorted.decor.common.block.tileentity.NeonSignTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
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
		if (ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
			ctx.get().enqueueWork(() -> {
				TileEntity tileentity = Minecraft.getInstance().player.getEntityWorld().getTileEntity(this.pos);

				// Make sure TileEntity exists
				if (!(tileentity instanceof NeonSignTileEntity)) {
					tileentity = new NeonSignTileEntity();
					tileentity.setWorldAndPos(Minecraft.getInstance().player.getEntityWorld(), this.pos);
				}

				Minecraft.getInstance().displayGuiScreen(new NeonSignScreen((NeonSignTileEntity) tileentity));
			});
			ctx.get().setPacketHandled(true);
		}
	}

}

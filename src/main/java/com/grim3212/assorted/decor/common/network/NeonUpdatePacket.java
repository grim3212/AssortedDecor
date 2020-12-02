package com.grim3212.assorted.decor.common.network;

import java.util.function.Supplier;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.tileentity.NeonSignTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class NeonUpdatePacket {

	private BlockPos pos;
	private String[] lines;

	public NeonUpdatePacket(BlockPos pos, IFormattableTextComponent[] linesIn) {
		this.pos = pos;

		// We want to save formatting codes
		this.lines = new String[] { IFormattableTextComponent.Serializer.toJson(linesIn[0]), IFormattableTextComponent.Serializer.toJson(linesIn[1]), IFormattableTextComponent.Serializer.toJson(linesIn[2]), IFormattableTextComponent.Serializer.toJson(linesIn[3]) };
	}

	public NeonUpdatePacket(BlockPos pos, String[] linesIn) {
		this.pos = pos;
		this.lines = linesIn;
	}

	public static NeonUpdatePacket decode(PacketBuffer buf) {
		BlockPos pos = buf.readBlockPos();

		String[] lines = new String[4];
		for (int i = 0; i < 4; i++) {
			lines[i] = buf.readString();
		}

		return new NeonUpdatePacket(pos, lines);
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(this.pos);

		for (int i = 0; i < 4; i++) {
			buf.writeString(this.lines[i]);
		}
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
			ctx.get().enqueueWork(() -> {
				PlayerEntity player = ctx.get().getSender();
				BlockState state = player.world.getBlockState(this.pos);
				TileEntity te = player.world.getTileEntity(this.pos);

				if (te instanceof NeonSignTileEntity) {
					NeonSignTileEntity neonSign = (NeonSignTileEntity) te;
					if (!neonSign.getOwner().getUniqueID().equals(player.getUniqueID())) {
						AssortedDecor.LOGGER.warn(AssortedDecor.MODNAME, "Player " + player.getName().getString() + " just tried to change a neon sign they don't own");
						return;
					}

					for (int i = 0; i < this.lines.length; ++i) {
						AssortedDecor.LOGGER.info(this.lines[i]);
						// Again do not strip away formatting codes
						neonSign.signText[i] = IFormattableTextComponent.Serializer.getComponentFromJson(this.lines[i]);
					}

					neonSign.markDirty();
					player.world.notifyBlockUpdate(this.pos, state, state, 3);
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}
}

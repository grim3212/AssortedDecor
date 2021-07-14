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
	private IFormattableTextComponent[] lines;

	public NeonUpdatePacket(BlockPos pos, IFormattableTextComponent[] linesIn) {
		this.pos = pos;

		// We want to save formatting codes
		this.lines = new IFormattableTextComponent[] { linesIn[0], linesIn[1], linesIn[2], linesIn[3] };
	}

	public static NeonUpdatePacket decode(PacketBuffer buf) {
		BlockPos pos = buf.readBlockPos();

		IFormattableTextComponent[] lines = new IFormattableTextComponent[4];
		for (int i = 0; i < 4; i++) {
			lines[i] = (IFormattableTextComponent) buf.readComponent();
		}

		return new NeonUpdatePacket(pos, lines);
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(this.pos);

		for (int i = 0; i < 4; i++) {
			buf.writeComponent(this.lines[i]);
		}
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
			ctx.get().enqueueWork(() -> {
				PlayerEntity player = ctx.get().getSender();
				BlockState state = player.level.getBlockState(this.pos);
				TileEntity te = player.level.getBlockEntity(this.pos);

				if (te instanceof NeonSignTileEntity) {
					NeonSignTileEntity neonSign = (NeonSignTileEntity) te;
					if (!neonSign.getOwner().getUUID().equals(player.getUUID())) {
						AssortedDecor.LOGGER.warn(AssortedDecor.MODNAME, "Player " + player.getName().getString() + " just tried to change a neon sign they don't own");
						return;
					}

					for (int i = 0; i < this.lines.length; ++i) {
						// Again do not strip away formatting codes
						neonSign.signText[i] = this.lines[i];
					}

					neonSign.setChanged();
					player.level.sendBlockUpdated(this.pos, state, state, 3);
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}
}

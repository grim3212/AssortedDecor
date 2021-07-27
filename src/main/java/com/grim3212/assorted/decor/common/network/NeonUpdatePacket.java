package com.grim3212.assorted.decor.common.network;

import java.util.function.Supplier;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.blockentity.NeonSignBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class NeonUpdatePacket {

	private BlockPos pos;
	private MutableComponent[] lines;

	public NeonUpdatePacket(BlockPos pos, MutableComponent[] linesIn) {
		this.pos = pos;

		// We want to save formatting codes
		this.lines = new MutableComponent[] { linesIn[0], linesIn[1], linesIn[2], linesIn[3] };
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

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
			ctx.get().enqueueWork(() -> {
				Player player = ctx.get().getSender();
				BlockState state = player.level.getBlockState(this.pos);
				BlockEntity te = player.level.getBlockEntity(this.pos);

				if (te instanceof NeonSignBlockEntity) {
					NeonSignBlockEntity neonSign = (NeonSignBlockEntity) te;
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

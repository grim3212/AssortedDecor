package com.grim3212.assorted.decor.common.block.tileentity;

import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NeonSignTileEntity extends BlockEntity {

	public static final TextComponent EMPTY = new TextComponent("");

	public MutableComponent[] signText = new MutableComponent[] { EMPTY, EMPTY, EMPTY, EMPTY };
	private UUID owner;
	public int mode = 0;

	public NeonSignTileEntity(BlockPos pos, BlockState state) {
		super(DecorTileEntityTypes.NEON_SIGN.get(), pos, state);
	}

	@OnlyIn(Dist.CLIENT)
	public MutableComponent getText(int line) {
		return this.signText[line];
	}

	public void setText(int line, MutableComponent signText) {
		this.signText[line] = signText;
	}

	@Override
	public boolean onlyOpCanSetNbt() {
		return true;
	}

	public void setOwner(Entity newOwner) {
		this.owner = newOwner.getUUID();
	}

	public Entity getOwner() {
		return this.owner != null && this.level instanceof ServerLevel ? ((ServerLevel) this.level).getEntity(this.owner) : null;
	}

	public boolean executeCommand(Player playerIn) {
		for (Component itextcomponent : this.signText) {
			Style style = itextcomponent == null ? null : itextcomponent.getStyle();
			if (style != null && style.getClickEvent() != null) {
				ClickEvent clickevent = style.getClickEvent();
				if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
					playerIn.getServer().getCommands().performCommand(this.getCommandSource((ServerPlayer) playerIn), clickevent.getValue());
				}
			}
		}

		return true;
	}

	public CommandSourceStack getCommandSource(@Nullable ServerPlayer playerIn) {
		String s = playerIn == null ? "Sign" : playerIn.getName().getString();
		Component itextcomponent = (Component) (playerIn == null ? new TextComponent("Sign") : playerIn.getDisplayName());
		return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(this.worldPosition), Vec2.ZERO, (ServerLevel) this.level, 2, s, itextcomponent, this.level.getServer(), playerIn);
	}

	@Override
	public CompoundTag save(CompoundTag compound) {
		super.save(compound);
		this.writePacketNBT(compound);
		return compound;
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.readPacketNBT(nbt);
	}

	public void writePacketNBT(CompoundTag cmp) {
		cmp.putInt("Mode", mode);
		cmp.putUUID("Owner", owner);

		for (int i = 0; i < 4; ++i) {
			String s = MutableComponent.Serializer.toJson(this.signText[i]);
			cmp.putString("Text" + (i + 1), s);
		}
	}

	public void readPacketNBT(CompoundTag cmp) {
		this.mode = cmp.getInt("Mode");
		this.owner = cmp.getUUID("Owner");

		for (int i = 0; i < 4; ++i) {
			String s = cmp.getString("Text" + (i + 1));
			MutableComponent itextcomponent = MutableComponent.Serializer.fromJson(s.isEmpty() ? "\"\"" : s);
			if (this.level instanceof ServerLevel) {
				try {
					this.signText[i] = ComponentUtils.updateForEntity(this.getCommandSource((ServerPlayer) null), itextcomponent, (Entity) null, 0);
				} catch (CommandSyntaxException commandsyntaxexception) {
					this.signText[i] = itextcomponent;
				}
			} else {
				this.signText[i] = itextcomponent;
			}
		}
	}

	@Override
	public CompoundTag getUpdateTag() {
		return save(new CompoundTag());
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag nbtTagCompound = new CompoundTag();
		writePacketNBT(nbtTagCompound);
		return new ClientboundBlockEntityDataPacket(this.worldPosition, 1, nbtTagCompound);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		this.readPacketNBT(pkt.getTag());
		if (level instanceof ClientLevel) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
		}
	}
}

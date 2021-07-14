package com.grim3212.assorted.decor.common.block.tileentity;

import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NeonSignTileEntity extends TileEntity {

	public static final StringTextComponent EMPTY = new StringTextComponent("");

	public IFormattableTextComponent[] signText = new IFormattableTextComponent[] { EMPTY, EMPTY, EMPTY, EMPTY };
	private UUID owner;
	public int mode = 0;

	public NeonSignTileEntity() {
		super(DecorTileEntityTypes.NEON_SIGN.get());
	}

	@OnlyIn(Dist.CLIENT)
	public IFormattableTextComponent getText(int line) {
		return this.signText[line];
	}

	public void setText(int line, IFormattableTextComponent signText) {
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
		return this.owner != null && this.level instanceof ServerWorld ? ((ServerWorld) this.level).getEntity(this.owner) : null;
	}

	public boolean executeCommand(PlayerEntity playerIn) {
		for (ITextComponent itextcomponent : this.signText) {
			Style style = itextcomponent == null ? null : itextcomponent.getStyle();
			if (style != null && style.getClickEvent() != null) {
				ClickEvent clickevent = style.getClickEvent();
				if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
					playerIn.getServer().getCommands().performCommand(this.getCommandSource((ServerPlayerEntity) playerIn), clickevent.getValue());
				}
			}
		}

		return true;
	}

	public CommandSource getCommandSource(@Nullable ServerPlayerEntity playerIn) {
		String s = playerIn == null ? "Sign" : playerIn.getName().getString();
		ITextComponent itextcomponent = (ITextComponent) (playerIn == null ? new StringTextComponent("Sign") : playerIn.getDisplayName());
		return new CommandSource(ICommandSource.NULL, Vector3d.atCenterOf(this.worldPosition), Vector2f.ZERO, (ServerWorld) this.level, 2, s, itextcomponent, this.level.getServer(), playerIn);
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		super.save(compound);
		this.writePacketNBT(compound);
		return compound;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		this.readPacketNBT(nbt);
	}

	public void writePacketNBT(CompoundNBT cmp) {
		cmp.putInt("Mode", mode);
		cmp.putUUID("Owner", owner);

		for (int i = 0; i < 4; ++i) {
			String s = IFormattableTextComponent.Serializer.toJson(this.signText[i]);
			cmp.putString("Text" + (i + 1), s);
		}
	}

	public void readPacketNBT(CompoundNBT cmp) {
		this.mode = cmp.getInt("Mode");
		this.owner = cmp.getUUID("Owner");

		for (int i = 0; i < 4; ++i) {
			String s = cmp.getString("Text" + (i + 1));
			IFormattableTextComponent itextcomponent = IFormattableTextComponent.Serializer.fromJson(s.isEmpty() ? "\"\"" : s);
			if (this.level instanceof ServerWorld) {
				try {
					this.signText[i] = TextComponentUtils.updateForEntity(this.getCommandSource((ServerPlayerEntity) null), itextcomponent, (Entity) null, 0);
				} catch (CommandSyntaxException commandsyntaxexception) {
					this.signText[i] = itextcomponent;
				}
			} else {
				this.signText[i] = itextcomponent;
			}
		}
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return save(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbtTagCompound = new CompoundNBT();
		writePacketNBT(nbtTagCompound);
		return new SUpdateTileEntityPacket(this.worldPosition, 1, nbtTagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		this.readPacketNBT(pkt.getTag());
		if (level instanceof ClientWorld) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
		}
	}
}

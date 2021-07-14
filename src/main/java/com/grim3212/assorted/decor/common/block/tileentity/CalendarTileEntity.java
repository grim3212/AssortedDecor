package com.grim3212.assorted.decor.common.block.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

public class CalendarTileEntity extends TileEntity {

	public CalendarTileEntity() {
		super(DecorTileEntityTypes.CALENDAR.get());
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		this.readPacketNBT(nbt);
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		super.save(compound);
		this.writePacketNBT(compound);
		return compound;
	}
	
	public void writePacketNBT(CompoundNBT cmp) {
	}

	public void readPacketNBT(CompoundNBT cmp) {
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
		requestModelDataUpdate();
		if (level instanceof ClientWorld) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
		}
	}
}

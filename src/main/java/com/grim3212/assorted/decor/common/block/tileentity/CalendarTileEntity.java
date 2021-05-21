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
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		this.readPacketNBT(nbt);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		this.writePacketNBT(compound);
		return compound;
	}
	
	public void writePacketNBT(CompoundNBT cmp) {
	}

	public void readPacketNBT(CompoundNBT cmp) {
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbtTagCompound = new CompoundNBT();
		writePacketNBT(nbtTagCompound);
		return new SUpdateTileEntityPacket(this.pos, 1, nbtTagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		this.readPacketNBT(pkt.getNbtCompound());
		requestModelDataUpdate();
		if (world instanceof ClientWorld) {
			world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 0);
		}
	}
}

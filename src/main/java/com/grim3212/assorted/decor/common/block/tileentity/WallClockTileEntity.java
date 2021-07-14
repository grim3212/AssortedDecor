package com.grim3212.assorted.decor.common.block.tileentity;

import com.grim3212.assorted.decor.common.block.WallClockBlock;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;

public class WallClockTileEntity extends TileEntity implements ITickableTileEntity {

	public WallClockTileEntity() {
		super(DecorTileEntityTypes.WALL_CLOCK.get());
	}

	private int time = 0;

	public int getTime() {
		return time;
	}

	private double field_94239_h;
	private double field_94240_i;

	@Override
	public void tick() {
		double d0 = 0.0D;

		if (getLevel() != null) {
			float f = getLevel().getTimeOfDay(1.0F);
			d0 = (double) f;

			if (getLevel().dimensionType().hasFixedTime()) {
				d0 = Math.random();
			}
		}

		double d1;

		for (d1 = d0 - field_94239_h; d1 < -0.5D; ++d1) {
			;
		}

		while (d1 >= 0.5D) {
			--d1;
		}

		d1 = MathHelper.clamp(d1, -1.0D, 1.0D);
		field_94240_i += d1 * 0.1D;
		field_94240_i *= 0.8D;
		field_94239_h += field_94240_i;

		int i;
		int numFrames = 64;
		for (i = (int) ((this.field_94239_h + 1.0D) * (double) numFrames) % numFrames; i < 0; i = (i + numFrames) % numFrames) {
			;
		}
		if (i != time) {
			time = i;
			BlockState state = this.getBlockState();
			this.getLevel().setBlockAndUpdate(getBlockPos(), state.setValue(WallClockBlock.TIME, getTime()));
		}
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

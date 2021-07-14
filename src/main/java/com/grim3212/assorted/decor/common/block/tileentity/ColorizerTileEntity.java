package com.grim3212.assorted.decor.common.block.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.registries.ForgeRegistries;

public class ColorizerTileEntity extends TileEntity {

	//TODO: Color overrides
	public static final ModelProperty<BlockState> BLOCK_STATE = new ModelProperty<BlockState>();
	protected BlockState blockState = Blocks.AIR.defaultBlockState();

	public ColorizerTileEntity() {
		super(DecorTileEntityTypes.COLORIZER.get());
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
		if (this.blockState.getBlock().getRegistryName() != null)
			cmp.put("stored_state", NBTUtil.writeBlockState(this.blockState));
		else
			cmp.put("stored_state", NBTUtil.writeBlockState(Blocks.AIR.defaultBlockState()));
	}

	public void readPacketNBT(CompoundNBT cmp) {
		this.blockState = NBTUtil.readBlockState(cmp.getCompound("stored_state"));
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

	@Override
	public IModelData getModelData() {
		return new ModelDataMap.Builder().withInitial(BLOCK_STATE, blockState).build();
	}

	public BlockState getStoredBlockState() {
		return blockState;
	}

	public void setStoredBlockState(BlockState blockState) {
		this.blockState = blockState;

		if (level != null) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
			level.getLightEngine().checkBlock(getBlockPos());
			if (!level.isClientSide) {
				level.blockUpdated(worldPosition, getBlockState().getBlock());
			}
		}
	}

	public void setStoredBlockState(String registryName) {
		this.setStoredBlockState(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(registryName)).defaultBlockState());
	}
}

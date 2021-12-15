package com.grim3212.assorted.decor.common.block.blockentity;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.registries.ForgeRegistries;

public class ColorizerBlockEntity extends BlockEntity {

	// TODO: Color overrides
	public static final ModelProperty<BlockState> BLOCK_STATE = new ModelProperty<BlockState>();
	protected BlockState blockState = Blocks.AIR.defaultBlockState();

	public ColorizerBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
	}

	public ColorizerBlockEntity(BlockPos pos, BlockState state) {
		super(DecorBlockEntityTypes.COLORIZER.get(), pos, state);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.blockState = NbtUtils.readBlockState(nbt.getCompound("stored_state"));
	}

	@Override
	protected void saveAdditional(CompoundTag cmp) {
		super.saveAdditional(cmp);
		if (this.blockState.getBlock().getRegistryName() != null)
			cmp.put("stored_state", NbtUtils.writeBlockState(this.blockState));
		else
			cmp.put("stored_state", NbtUtils.writeBlockState(Blocks.AIR.defaultBlockState()));
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithoutMetadata();
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		requestModelDataUpdate();
		if (level instanceof ClientLevel) {
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

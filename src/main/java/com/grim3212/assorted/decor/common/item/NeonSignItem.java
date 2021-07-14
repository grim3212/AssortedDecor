package com.grim3212.assorted.decor.common.item;

import javax.annotation.Nullable;

import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.block.tileentity.NeonSignTileEntity;
import com.grim3212.assorted.decor.common.network.NeonOpenPacket;
import com.grim3212.assorted.decor.common.network.PacketHandler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NeonSignItem extends WallOrFloorItem {

	public NeonSignItem(Item.Properties props) {
		super(DecorBlocks.NEON_SIGN.get(), DecorBlocks.NEON_SIGN_WALL.get(), props);
	}

	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos pos, World worldIn, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
		boolean flag = super.updateCustomBlockEntityTag(pos, worldIn, player, stack, state);
		if (!worldIn.isClientSide && !flag && player != null) {
			((NeonSignTileEntity) worldIn.getBlockEntity(pos)).setOwner(player);
			PacketHandler.sendTo((ServerPlayerEntity) player, new NeonOpenPacket(pos));
		}

		return flag;
	}
}
package com.grim3212.assorted.decor.common.item;

import javax.annotation.Nullable;

import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.block.blockentity.NeonSignBlockEntity;
import com.grim3212.assorted.decor.common.network.NeonOpenPacket;
import com.grim3212.assorted.decor.common.network.PacketHandler;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class NeonSignItem extends StandingAndWallBlockItem {

	public NeonSignItem(Item.Properties props) {
		super(DecorBlocks.NEON_SIGN.get(), DecorBlocks.NEON_SIGN_WALL.get(), props);
	}

	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos pos, Level worldIn, @Nullable Player player, ItemStack stack, BlockState state) {
		boolean flag = super.updateCustomBlockEntityTag(pos, worldIn, player, stack, state);
		if (!worldIn.isClientSide && !flag && player != null) {
			((NeonSignBlockEntity) worldIn.getBlockEntity(pos)).setOwner(player);
			PacketHandler.sendTo((ServerPlayer) player, new NeonOpenPacket(pos));
		}

		return flag;
	}
}
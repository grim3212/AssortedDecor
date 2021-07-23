package com.grim3212.assorted.decor.common.proxy;

import com.grim3212.assorted.decor.common.block.tileentity.NeonSignTileEntity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IProxy {
	default void starting() {
	}

	default void produceSmoke(Level world, BlockPos pos, double xMod, double yMod, double zMod, int amount, boolean makelarge) {
	}

	default Player getClientPlayer() {
		return null;
	}

	default void openNeonSign(NeonSignTileEntity tile) {
	}

	default void handleOpenNeonSign(BlockPos pos) {
	}
}

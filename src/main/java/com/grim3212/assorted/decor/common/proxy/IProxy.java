package com.grim3212.assorted.decor.common.proxy;

import net.minecraft.world.entity.player.Player;

import com.grim3212.assorted.decor.common.block.blockentity.NeonSignBlockEntity;

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

	default void openNeonSign(NeonSignBlockEntity tile) {
	}

	default void handleOpenNeonSign(BlockPos pos) {
	}
}

package com.grim3212.assorted.decor.common.proxy;

import javax.annotation.Nullable;

import com.grim3212.assorted.decor.common.block.tileentity.NeonSignTileEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IProxy {
	default void starting() {
	}

	default void produceSmoke(World world, BlockPos pos, double xMod, double yMod, double zMod, int amount, boolean makelarge) {
	}

	default PlayerEntity getClientPlayer() {
		return null;
	}

	default void openNeonSign(NeonSignTileEntity tile, @Nullable PlayerEntity player) {
	}
}

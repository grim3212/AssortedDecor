package com.grim3212.assorted.decor.common.proxy;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IProxy {
	default void starting() {
	}

	default void produceSmoke(World world, BlockPos pos, double xMod, double yMod, double zMod, int amount, boolean makelarge) {
	}
}

package com.grim3212.assorted.decor.api.colorizer;

import javax.annotation.Nullable;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

public interface ICanColor {

	@Nullable
	public DyeColor currentColor(BlockState state);

	public BlockState stateForColor(BlockState state, DyeColor color);

}

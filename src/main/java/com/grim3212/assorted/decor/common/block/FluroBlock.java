package com.grim3212.assorted.decor.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class FluroBlock extends Block {
	
	public FluroBlock(MaterialColor color) {
		super(Block.Properties.of(Material.GLASS, color).sound(SoundType.GLASS).strength(0.2F, 1.0F).lightLevel(state -> 15).sound(SoundType.GLASS));
	}

}

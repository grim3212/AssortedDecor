package com.grim3212.assorted.decor.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class FluroBlock extends Block {
	
	public FluroBlock(MaterialColor color) {
		super(Block.Properties.of(Material.GLASS, color).sound(SoundType.GLASS).strength(0.2F, 1.0F).lightLevel(state -> 15).sound(SoundType.GLASS));
	}

}

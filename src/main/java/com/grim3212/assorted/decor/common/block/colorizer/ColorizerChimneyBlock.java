package com.grim3212.assorted.decor.common.block.colorizer;

import java.util.Random;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.DecorBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ColorizerChimneyBlock extends ColorizerBlock {

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (worldIn.getBlockState(pos.below()).getBlock() == Blocks.FIRE) {
			if (worldIn.random.nextInt(2) == 0) {
				int smokeheight = 1;
				while (worldIn.getBlockState(pos.above(smokeheight)).getBlock() == DecorBlocks.COLORIZER_CHIMNEY.get()) {
					smokeheight++;
				}

				AssortedDecor.proxy.produceSmoke(worldIn, pos.above(smokeheight), 0.5D, 0.0D, 0.5D, 1, true);
			}
		}
	}
}

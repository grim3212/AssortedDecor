package com.grim3212.assorted.decor.common.item;

import com.grim3212.assorted.decor.common.block.DecorBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

public class AsphaltItem extends Item {

	public AsphaltItem(Properties props) {
		super(props);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);

		if (state.is(Tags.Blocks.STONE)) {
			context.getItemInHand().shrink(1);
			level.setBlock(pos, DecorBlocks.ROADWAY.get().defaultBlockState(), 3);
			return InteractionResult.sidedSuccess(level.isClientSide());
		}

		return super.useOn(context);
	}
}

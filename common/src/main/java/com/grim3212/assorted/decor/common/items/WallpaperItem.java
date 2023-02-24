package com.grim3212.assorted.decor.common.items;

import com.grim3212.assorted.decor.common.entity.WallpaperEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class WallpaperItem extends Item {

	public WallpaperItem(Item.Properties props) {
		super(props);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Direction facing = context.getClickedFace();
		Player playerIn = context.getPlayer();
		Level worldIn = context.getLevel();
		BlockPos pos = context.getClickedPos();

		if (facing != Direction.DOWN && facing != Direction.UP && !worldIn.isOutsideBuildHeight(pos) && playerIn.mayUseItemAt(pos, facing, context.getItemInHand())) {
			WallpaperEntity wallpaper = new WallpaperEntity(worldIn, pos.relative(facing), facing);

			if (wallpaper != null && wallpaper.survives()) {
				if (!worldIn.isClientSide) {
					wallpaper.playPlacementSound();
					worldIn.addFreshEntity(wallpaper);
				}

				context.getItemInHand().shrink(1);
			}

			return InteractionResult.SUCCESS;
		} else {
			return InteractionResult.FAIL;
		}
	}
}
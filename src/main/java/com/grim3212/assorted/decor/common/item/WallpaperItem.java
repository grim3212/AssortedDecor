package com.grim3212.assorted.decor.common.item;

import com.grim3212.assorted.decor.common.entity.WallpaperEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WallpaperItem extends Item {

	public WallpaperItem(Item.Properties props) {
		super(props);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		Direction facing = context.getClickedFace();
		PlayerEntity playerIn = context.getPlayer();
		World worldIn = context.getLevel();
		BlockPos pos = context.getClickedPos();

		if (facing != Direction.DOWN && facing != Direction.UP && playerIn.mayUseItemAt(pos.relative(facing), facing, context.getItemInHand())) {
			WallpaperEntity wallpaper = new WallpaperEntity(worldIn, pos.relative(facing), facing);

			if (wallpaper != null && wallpaper.survives()) {
				if (!worldIn.isClientSide) {
					wallpaper.playPlacementSound();
					worldIn.addFreshEntity(wallpaper);
				}

				context.getItemInHand().shrink(1);
			}

			return ActionResultType.SUCCESS;
		} else {
			return ActionResultType.FAIL;
		}
	}
}
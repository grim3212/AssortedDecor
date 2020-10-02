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
	public ActionResultType onItemUse(ItemUseContext context) {
		Direction facing = context.getFace();
		PlayerEntity playerIn = context.getPlayer();
		World worldIn = context.getWorld();
		BlockPos pos = context.getPos();

		if (facing != Direction.DOWN && facing != Direction.UP && playerIn.canPlayerEdit(pos.offset(facing), facing, context.getItem())) {
			WallpaperEntity wallpaper = new WallpaperEntity(worldIn, pos.offset(facing), facing);

			if (wallpaper != null && wallpaper.onValidSurface()) {
				if (!worldIn.isRemote) {
					wallpaper.playPlaceSound();
					worldIn.addEntity(wallpaper);
				}

				context.getItem().shrink(1);
			}

			return ActionResultType.SUCCESS;
		} else {
			return ActionResultType.FAIL;
		}
	}
}
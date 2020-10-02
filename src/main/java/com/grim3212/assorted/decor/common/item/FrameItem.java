package com.grim3212.assorted.decor.common.item;

import com.grim3212.assorted.decor.common.entity.FrameEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FrameItem extends Item {

	private final FrameMaterial material;

	public FrameItem(FrameMaterial material, Item.Properties props) {
		super(props);
		this.material = material;
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		Direction facing = context.getFace();
		PlayerEntity playerIn = context.getPlayer();
		World worldIn = context.getWorld();
		BlockPos pos = context.getPos();

		if (facing != Direction.DOWN && facing != Direction.UP && playerIn.canPlayerEdit(pos.offset(facing), facing, context.getItem())) {
			FrameEntity frame = new FrameEntity(worldIn, pos.offset(facing), facing, this.material);

			if (frame != null && frame.onValidSurface()) {
				if (!worldIn.isRemote) {
					frame.playPlaceSound();
					worldIn.addEntity(frame);
				}

				context.getItem().shrink(1);
			}

			return ActionResultType.SUCCESS;
		} else {
			return ActionResultType.FAIL;
		}
	}

	public static enum FrameMaterial {
		WOOD, IRON;

		public Item getFrameItem() {
			if (this == WOOD) {
				return DecorItems.WOOD_FRAME.get();
			} else {
				return DecorItems.IRON_FRAME.get();
			}
		}

		public Class<? extends Item> effectiveTool() {
			if (this == WOOD) {
				return AxeItem.class;
			} else {
				return PickaxeItem.class;
			}
		}
	}
}
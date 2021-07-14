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
	public ActionResultType useOn(ItemUseContext context) {
		Direction facing = context.getClickedFace();
		PlayerEntity playerIn = context.getPlayer();
		World worldIn = context.getLevel();
		BlockPos pos = context.getClickedPos();

		if (facing != Direction.DOWN && facing != Direction.UP && playerIn.mayUseItemAt(pos.relative(facing), facing, context.getItemInHand())) {
			FrameEntity frame = new FrameEntity(worldIn, pos.relative(facing), facing, this.material);

			if (frame != null && frame.survives()) {
				if (!worldIn.isClientSide) {
					frame.playPlacementSound();
					worldIn.addFreshEntity(frame);
				}

				context.getItemInHand().shrink(1);
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
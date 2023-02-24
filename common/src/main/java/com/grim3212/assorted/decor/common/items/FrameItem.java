package com.grim3212.assorted.decor.common.items;

import com.grim3212.assorted.decor.common.entity.FrameEntity;
import com.grim3212.assorted.decor.common.entity.IronFrameEntity;
import com.grim3212.assorted.decor.common.entity.WoodFrameEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class FrameItem extends Item {

    private final FrameMaterial material;

    public FrameItem(FrameMaterial material, Item.Properties props) {
        super(props);
        this.material = material;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction facing = context.getClickedFace();
        Player playerIn = context.getPlayer();
        Level worldIn = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (facing != Direction.DOWN && facing != Direction.UP && playerIn.mayUseItemAt(pos.relative(facing), facing, context.getItemInHand())) {
            FrameEntity frame = this.material == FrameMaterial.WOOD ? new WoodFrameEntity(worldIn, pos.relative(facing), facing) : new IronFrameEntity(worldIn, pos.relative(facing), facing);

            if (frame != null && frame.survives()) {
                if (!worldIn.isClientSide) {
                    frame.playPlacementSound();
                    worldIn.addFreshEntity(frame);
                }

                context.getItemInHand().shrink(1);
            }

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
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
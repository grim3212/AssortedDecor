package com.grim3212.assorted.decor.common.items;


import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ColorChangingItem extends BlockItem {

    public ColorChangingItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(BLOCK_STATE_TAG)) {
            DyeColor color = DyeColor.byName(NBTHelper.getString(NBTHelper.getTag(stack.getTag(), BLOCK_STATE_TAG), "color"), DyeColor.WHITE);
            return super.getDescriptionId(stack) + "_" + color.getName();
        }

        return super.getDescriptionId(stack) + "_" + DyeColor.WHITE.getName();
    }
}

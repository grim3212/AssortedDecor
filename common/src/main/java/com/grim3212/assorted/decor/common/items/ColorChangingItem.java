package com.grim3212.assorted.decor.common.items;


import com.grim3212.assorted.decor.common.blocks.ColorChangingBlock;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;

public class ColorChangingItem extends BlockItem {

    public ColorChangingItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public Component getName(ItemStack stack) {
        DyeColor color = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).get(ColorChangingBlock.COLOR);
        return Component.translatable(this.getDescriptionId() + "_" + (color != null ? color : DyeColor.WHITE).getName());
    }
}

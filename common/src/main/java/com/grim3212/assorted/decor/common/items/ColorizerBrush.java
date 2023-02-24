package com.grim3212.assorted.decor.common.items;

import com.grim3212.assorted.decor.DecorConfig;
import com.grim3212.assorted.lib.core.item.ExtraPropertyItem;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ColorizerBrush extends ExtraPropertyItem {

    public ColorizerBrush(Properties properties) {
        super(properties.durability(16));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), NBTHelper.getTag(stack, "stored_state"));

        if (state.getBlock() == Blocks.AIR) {
            tooltip.add(Component.translatable("tooltip.colorizer_brush.empty"));
        } else {
            tooltip.add(Component.translatable("tooltip.colorizer_brush.stored", state.getBlock().getName()));
        }
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return DecorConfig.Common.colorizerBrushCount.getValue();
    }
}

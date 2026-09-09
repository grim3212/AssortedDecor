package com.grim3212.assorted.decor.common.items;

import com.grim3212.assorted.decor.DecorCommonMod;
import com.grim3212.assorted.lib.core.item.ExtraPropertyItem;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class ColorizerBrush extends ExtraPropertyItem {

    public ColorizerBrush(Properties properties) {
        super(properties.durability(16));
    }

    // Item.appendHoverText is deprecated in favour of tooltip-providing data components, but the line
    // below is derived from the brush's stored block state, which lives in CUSTOM_DATA and has no
    // component of its own. Vanilla still overrides this method for the same reason.
    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, display, tooltip, flagIn);
        BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK, NBTHelper.getTag(stack, "stored_state"));

        if (state.getBlock() == Blocks.AIR) {
            tooltip.accept(Component.translatable("tooltip.colorizer_brush.empty"));
        } else {
            tooltip.accept(Component.translatable("tooltip.colorizer_brush.stored", state.getBlock().getName()));
        }
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return DecorCommonMod.COMMON_CONFIG.colorizerBrushCount.get();
    }
}

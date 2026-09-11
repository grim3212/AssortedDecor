package com.grim3212.assorted.decor.common.items;

import com.grim3212.assorted.decor.DecorCommonMod;
import com.grim3212.assorted.lib.core.item.ExtraPropertyItem;
import net.minecraft.world.item.ItemStack;

/**
 * Picks up a block and paints it onto colorizers. Its tooltip is a default
 * {@link ColorizerBrushInfo} component.
 */
public class ColorizerBrush extends ExtraPropertyItem {

    public ColorizerBrush(Properties properties) {
        super(properties.durability(16).component(DecorDataComponents.COLORIZER_BRUSH_INFO.get(), ColorizerBrushInfo.INSTANCE));
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return DecorCommonMod.COMMON_CONFIG.colorizerBrushCount.get();
    }
}

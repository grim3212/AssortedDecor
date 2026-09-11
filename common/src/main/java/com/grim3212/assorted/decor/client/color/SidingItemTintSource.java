package com.grim3212.assorted.decor.client.color;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.common.blocks.ColorChangingBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Tints a siding item with the map colour of the dye in its {@link DataComponents#BLOCK_STATE}, as
 * {@code ColorChangingItem} reads it for the name. Reached through
 * {@code "tints": [{"type": "assorteddecor:siding"}]} in the item model.
 */
public record SidingItemTintSource() implements ItemTintSource {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "siding");
    public static final MapCodec<SidingItemTintSource> MAP_CODEC = MapCodec.unit(new SidingItemTintSource());

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        DyeColor color = itemStack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY)
                .get(ColorChangingBlock.COLOR);
        if (color == null) {
            return -1;
        }

        // Colours carry an alpha channel now, so an unpacked RGB has to be made opaque explicitly.
        return ARGB.opaque(color.getMapColor().col);
    }

    @Override
    public MapCodec<SidingItemTintSource> type() {
        return MAP_CODEC;
    }
}

package com.grim3212.assorted.decor.client.color;

import com.grim3212.assorted.decor.Constants;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

/**
 * Tints an item with the map colour of the block it places, so the fluro blocks share one texture.
 * Reached through {@code "tints": [{"type": "assorteddecor:block_map_color"}]} in the item model.
 */
public record BlockMapColorItemTintSource() implements ItemTintSource {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block_map_color");
    public static final MapCodec<BlockMapColorItemTintSource> MAP_CODEC = MapCodec.unit(new BlockMapColorItemTintSource());

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        Block block = Block.byItem(itemStack.getItem());
        // Colours carry an alpha channel now, so an unpacked RGB has to be made opaque explicitly.
        return block != Blocks.AIR ? ARGB.opaque(block.defaultMapColor().col) : -1;
    }

    @Override
    public MapCodec<BlockMapColorItemTintSource> type() {
        return MAP_CODEC;
    }
}

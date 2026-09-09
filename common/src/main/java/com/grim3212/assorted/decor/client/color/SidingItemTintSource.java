package com.grim3212.assorted.decor.client.color;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Tints a colour-changing item (the sidings) with the map colour of the dye recorded in its
 * {@code BlockStateTag}, matching what {@code ColorChangingItem} reads for its name.
 * <p>
 * Item tinting is data-driven in 26.2, so the item model json has to carry
 * {@code "tints": [{"type": "assorteddecor:siding"}]} for this to be reached.
 */
public record SidingItemTintSource() implements ItemTintSource {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "siding");
    public static final MapCodec<SidingItemTintSource> MAP_CODEC = MapCodec.unit(new SidingItemTintSource());

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        if (!NBTHelper.hasTag(itemStack, "BlockStateTag")) {
            return -1;
        }

        CompoundTag blockState = NBTHelper.getTag(itemStack, "BlockStateTag");
        if (!blockState.contains("color")) {
            return -1;
        }

        DyeColor color = DyeColor.byName(NBTHelper.getString(blockState, "color"), DyeColor.WHITE);
        // Colours carry an alpha channel now, so an unpacked RGB has to be made opaque explicitly.
        return ARGB.opaque(color.getMapColor().col);
    }

    @Override
    public MapCodec<SidingItemTintSource> type() {
        return MAP_CODEC;
    }
}

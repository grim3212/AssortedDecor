package com.grim3212.assorted.decor.client.color;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Tints a colorizer item as its stored block state would be tinted, by evaluating that block's
 * {@link BlockTintSource} with no level. Reached through
 * {@code "tints": [{"type": "assorteddecor:colorizer"}]} in the item model.
 */
public record ColorizerItemTintSource() implements ItemTintSource {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "colorizer");
    public static final MapCodec<ColorizerItemTintSource> MAP_CODEC = MapCodec.unit(new ColorizerItemTintSource());

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        if (!NBTHelper.hasTag(itemStack, "stored_state")) {
            return -1;
        }

        // Registry implements HolderLookup.RegistryLookup itself now, so there is no asLookup() view.
        BlockState stored = NbtUtils.readBlockState(BuiltInRegistries.BLOCK, NBTHelper.getTag(itemStack, "stored_state"));
        if (stored.isAir()) {
            return -1;
        }

        BlockTintSource source = ClientServices.CLIENT.getBlockColors().getTintSource(stored, 0);
        return source != null ? source.color(stored) : -1;
    }

    @Override
    public MapCodec<ColorizerItemTintSource> type() {
        return MAP_CODEC;
    }
}

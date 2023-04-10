package com.grim3212.assorted.decor.common.helpers;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.DecorCommonMod;
import com.grim3212.assorted.decor.common.blocks.ColorChangingBlock;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.FluroBlock;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class DecorCreativeItems {

    private static List<ItemStack> getCreativeItems() {
        CreativeTabItems items = new CreativeTabItems();

        if (DecorCommonMod.COMMON_CONFIG.colorizerEnabled.get()) {
            items.add(DecorItems.COLORIZER_BRUSH.get());
            DecorBlocks.colorizerBlocks().forEach(x -> items.add(x.get()));
        }

        if (DecorCommonMod.COMMON_CONFIG.fluroEnabled.get()) {
            FluroBlock.FLURO_BY_DYE.values().forEach(x -> items.add(x.get()));
            items.add(DecorBlocks.ILLUMINATION_TUBE.get());
            items.add(DecorBlocks.ILLUMINATION_PLATE.get());
        }

        if (DecorCommonMod.COMMON_CONFIG.neonSignEnabled.get()) {
            items.add(DecorItems.NEON_SIGN.get());
        }

        if (DecorCommonMod.COMMON_CONFIG.hangeablesEnabled.get()) {
            items.add(DecorBlocks.CALENDAR.get());
            items.add(DecorBlocks.WALL_CLOCK.get());
            items.add(DecorItems.WALLPAPER.get());
            items.add(DecorItems.WOOD_FRAME.get());
            items.add(DecorItems.IRON_FRAME.get());
        }

        if (DecorCommonMod.COMMON_CONFIG.cageEnabled.get()) {
            items.add(DecorBlocks.CAGE.get());
        }

        if (DecorCommonMod.COMMON_CONFIG.planterPotEnabled.get()) {
            items.add(DecorBlocks.PLANTER_POT.get());
            items.add(DecorItems.UNFIRED_PLANTER_POT.get());
        }

        if (DecorCommonMod.COMMON_CONFIG.decorationsEnabled.get()) {
            items.add(DecorItems.UNFIRED_CLAY_DECORATION.get());
            items.add(DecorBlocks.CLAY_DECORATION.get());
            items.add(DecorBlocks.BONE_DECORATION.get());
            items.add(DecorBlocks.PAPER_LANTERN.get());
            items.add(DecorBlocks.BONE_LANTERN.get());
            items.add(DecorBlocks.IRON_LANTERN.get());
            items.add(DecorBlocks.FOUNTAIN.get());
            items.add(DecorBlocks.STONE_PATH.get());
            items.add(DecorBlocks.DECORATIVE_STONE.get());
            items.add(DecorBlocks.SIDEWALK.get());
        }

        if (DecorCommonMod.COMMON_CONFIG.extrasEnabled.get()) {
            items.add(DecorItems.CHAIN_LINK.get());
            items.add(DecorBlocks.CHAIN_LINK_FENCE.get());
            items.add(DecorBlocks.CHAIN_LINK_DOOR.get());
            items.add(DecorBlocks.QUARTZ_DOOR.get());
            items.add(DecorBlocks.GLASS_DOOR.get());
            items.add(DecorBlocks.STEEL_DOOR.get());
        }

        if (DecorCommonMod.COMMON_CONFIG.roadwaysEnabled.get()) {
            DecorBlocks.ROADWAY_COLORS.values().forEach(x -> items.add(x.get()));
            items.add(DecorBlocks.ROADWAY.get());
            items.add(DecorBlocks.ROADWAY_MANHOLE.get());
            items.add(DecorBlocks.ROADWAY_LIGHT.get());
            items.add(DecorItems.TARBALL.get());
            items.add(DecorItems.ASPHALT.get());
        }

        if (DecorCommonMod.COMMON_CONFIG.paintingEnabled.get()) {
            items.add(DecorItems.PAINT_ROLLER.get());
            DecorItems.PAINT_ROLLER_COLORS.values().forEach(x -> items.add(x.get()));
            Arrays.stream(DyeColor.values()).forEach(x -> {
                items.add(ColorChangingBlock.getColorStack(new ItemStack(DecorBlocks.SIDING_VERTICAL.get()), x));
                items.add(ColorChangingBlock.getColorStack(new ItemStack(DecorBlocks.SIDING_HORIZONTAL.get()), x));
            });
        }

        return items.getItems();
    }

    public static void init() {
        Services.PLATFORM.registerCreativeTab(new ResourceLocation(Constants.MOD_ID, "tab"), Component.translatable("itemGroup." + Constants.MOD_ID), () -> new ItemStack(DecorItems.WALLPAPER.get()), DecorCreativeItems::getCreativeItems);
    }

}

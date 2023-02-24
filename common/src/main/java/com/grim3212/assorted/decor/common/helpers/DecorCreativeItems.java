package com.grim3212.assorted.decor.common.helpers;

import com.grim3212.assorted.decor.DecorConfig;
import com.grim3212.assorted.decor.common.blocks.ColorChangingBlock;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.FluroBlock;
import com.grim3212.assorted.decor.common.items.DecorItems;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DecorCreativeItems {

    public static List<ItemStack> getCreativeItems() {
        CreativeTabItems items = new CreativeTabItems();

        if (DecorConfig.Common.partColorizerEnabled.getValue()) {
            items.add(DecorItems.COLORIZER_BRUSH.get());
            Arrays.stream(DecorBlocks.colorizerBlocks()).forEach(x -> items.add(x));
        }

        if (DecorConfig.Common.partFluroEnabled.getValue()) {
            FluroBlock.FLURO_BY_DYE.values().forEach(x -> items.add(x.get()));
            items.add(DecorBlocks.ILLUMINATION_TUBE.get());
            items.add(DecorBlocks.ILLUMINATION_PLATE.get());
        }

        if (DecorConfig.Common.partNeonSignEnabled.getValue()) {
            items.add(DecorItems.NEON_SIGN.get());
        }

        if (DecorConfig.Common.partHangeablesEnabled.getValue()) {
            items.add(DecorBlocks.CALENDAR.get());
            items.add(DecorBlocks.WALL_CLOCK.get());
            items.add(DecorItems.WALLPAPER.get());
            items.add(DecorItems.WOOD_FRAME.get());
            items.add(DecorItems.IRON_FRAME.get());
        }

        if (DecorConfig.Common.partCageEnabled.getValue()) {
            items.add(DecorBlocks.CAGE.get());
        }

        if (DecorConfig.Common.partPlanterPotEnabled.getValue()) {
            items.add(DecorBlocks.PLANTER_POT.get());
            items.add(DecorItems.UNFIRED_PLANTER_POT.get());
        }

        if (DecorConfig.Common.partDecorationsEnabled.getValue()) {
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

        if (DecorConfig.Common.partExtrasEnabled.getValue()) {
            items.add(DecorItems.CHAIN_LINK.get());
            items.add(DecorBlocks.CHAIN_LINK_FENCE.get());
            items.add(DecorBlocks.CHAIN_LINK_DOOR.get());
            items.add(DecorBlocks.QUARTZ_DOOR.get());
            items.add(DecorBlocks.GLASS_DOOR.get());
            items.add(DecorBlocks.STEEL_DOOR.get());
        }

        if (DecorConfig.Common.partRoadwaysEnabled.getValue()) {
            DecorBlocks.ROADWAY_COLORS.values().forEach(x -> items.add(x.get()));
            items.add(DecorBlocks.ROADWAY.get());
            items.add(DecorBlocks.ROADWAY_MANHOLE.get());
            items.add(DecorBlocks.ROADWAY_LIGHT.get());
            items.add(DecorItems.TARBALL.get());
            items.add(DecorItems.ASPHALT.get());
        }

        if (DecorConfig.Common.partPaintingEnabled.getValue()) {
            items.add(DecorItems.PAINT_ROLLER.get());
            DecorItems.PAINT_ROLLER_COLORS.values().forEach(x -> items.add(x.get()));
            Arrays.stream(DyeColor.values()).forEach(x -> {
                items.add(ColorChangingBlock.getColorStack(new ItemStack(DecorBlocks.SIDING_VERTICAL.get()), x));
                items.add(ColorChangingBlock.getColorStack(new ItemStack(DecorBlocks.SIDING_HORIZONTAL.get()), x));
            });
        }

        return items.getItems();
    }

    public static class CreativeTabItems {
        List<ItemStack> items = new ArrayList<>();

        public void add(ItemLike item) {
            items.add(new ItemStack(item));
        }

        public void add(ItemStack item) {
            items.add(item);
        }

        public List<ItemStack> getItems() {
            return this.items;
        }
    }

}

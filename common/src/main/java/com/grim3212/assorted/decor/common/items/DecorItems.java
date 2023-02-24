package com.grim3212.assorted.decor.common.items;

import com.google.common.collect.Maps;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.items.FrameItem.FrameMaterial;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

public class DecorItems {

    public static final IRegistryObject<WallpaperItem> WALLPAPER = register("wallpaper", () -> new WallpaperItem(new Item.Properties()));
    public static final IRegistryObject<FrameItem> WOOD_FRAME = register("wood_frame", () -> new FrameItem(FrameMaterial.WOOD, new Item.Properties()));
    public static final IRegistryObject<FrameItem> IRON_FRAME = register("iron_frame", () -> new FrameItem(FrameMaterial.IRON, new Item.Properties()));

    public static final IRegistryObject<ColorizerBrush> COLORIZER_BRUSH = register("colorizer_brush", () -> new ColorizerBrush(new Item.Properties()));
    public static final IRegistryObject<Item> UNFIRED_PLANTER_POT = register("unfired_planter_pot", () -> new Item(new Item.Properties()));
    public static final IRegistryObject<Item> UNFIRED_CLAY_DECORATION = register("unfired_clay_decoration", () -> new Item(new Item.Properties()));

    public static final IRegistryObject<NeonSignItem> NEON_SIGN = register("neon_sign", () -> new NeonSignItem(new Item.Properties().stacksTo(16)));

    public static final IRegistryObject<Item> TARBALL = register("tarball", () -> new Item(new Item.Properties()));
    public static final IRegistryObject<AsphaltItem> ASPHALT = register("asphalt", () -> new AsphaltItem(new Item.Properties()));

    public static final IRegistryObject<Item> PAINT_ROLLER = register("paint_roller", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final IRegistryObject<Item> CHAIN_LINK = register("chain_link", () -> new Item(new Item.Properties()));

    public static final Map<DyeColor, IRegistryObject<PaintRollerItem>> PAINT_ROLLER_COLORS = Maps.newHashMap();

    static {
        Arrays.stream(DyeColor.values()).forEach((color) -> PAINT_ROLLER_COLORS.put(color, register("paint_roller_" + color.getName(), () -> new PaintRollerItem(color, new Item.Properties()))));
    }

    private static <T extends Item> IRegistryObject<T> register(final String name, final Supplier<T> sup) {
        return DecorBlocks.ITEMS.register(name, sup);
    }

    public static void init() {
    }
}

package com.grim3212.assorted.decor.common.items;

import com.google.common.collect.Maps;
import com.grim3212.assorted.decor.common.sounds.DecorSounds;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.items.FrameItem.FrameMaterial;
import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public class DecorItems {

    public static final IRegistryObject<WallpaperItem> WALLPAPER = register("wallpaper", props -> new WallpaperItem(props));
    public static final IRegistryObject<FrameItem> WOOD_FRAME = register("wood_frame", props -> new FrameItem(FrameMaterial.WOOD, props));
    public static final IRegistryObject<FrameItem> IRON_FRAME = register("iron_frame", props -> new FrameItem(FrameMaterial.IRON, props));

    public static final IRegistryObject<ColorizerBrush> COLORIZER_BRUSH = register("colorizer_brush", props -> new ColorizerBrush(props));
    public static final IRegistryObject<Item> UNFIRED_PLANTER_POT = register("unfired_planter_pot", props -> new Item(props));
    public static final IRegistryObject<Item> UNFIRED_CLAY_DECORATION = register("unfired_clay_decoration", props -> new Item(props));

    public static final IRegistryObject<NeonSignItem> NEON_SIGN = register("neon_sign", props -> new NeonSignItem(props.stacksTo(16)));

    public static final IRegistryObject<Item> TARBALL = register("tarball", props -> new Item(props));
    public static final IRegistryObject<AsphaltItem> ASPHALT = register("asphalt", props -> new AsphaltItem(props));

    public static final IRegistryObject<Item> PAINT_ROLLER = register("paint_roller", props -> new Item(props.stacksTo(1)));
    public static final IRegistryObject<Item> CHAIN_LINK = register("chain_link", props -> new Item(props));

    public static final IRegistryObject<Item> GATE_GRATING = register("gate_grating", props -> new Item(props));
    public static final IRegistryObject<Item> GARAGE_PANEL = register("garage_panel", props -> new Item(props));
    public static final IRegistryObject<GateActivatorItem> GATE_TRUMPET = register("gate_trumpet", props -> new GateActivatorItem(DecorBlocks.CASTLE_GATE::get, DecorSounds.GATE_TRUMPET::get, 60, props.stacksTo(1)));
    public static final IRegistryObject<GateActivatorItem> GARAGE_REMOTE = register("garage_remote", props -> new GateActivatorItem(DecorBlocks.GARAGE_DOOR::get, DecorSounds.GARAGE_REMOTE::get, 18, props.stacksTo(1)));

    public static final Map<DyeColor, IRegistryObject<PaintRollerItem>> PAINT_ROLLER_COLORS = Maps.newEnumMap(DyeColor.class);

    static {
        Arrays.stream(DyeColor.values()).forEach((color) -> PAINT_ROLLER_COLORS.put(color, register("paint_roller_" + color.getName(), props -> new PaintRollerItem(color, props))));
    }

    private static <T extends Item> IRegistryObject<T> register(final String name, final Function<Item.Properties, ? extends T> factory) {
        // Since 1.21.2 every item has to know its own id before it is constructed, so the
        // properties are built here where the registration name is known. Without this the game
        // dies at registration with "Item id not set", which compiles perfectly happily.
        final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return DecorBlocks.ITEMS.register(name, () -> factory.apply(new Item.Properties().setId(key)));
    }

    public static void init() {
    }
}

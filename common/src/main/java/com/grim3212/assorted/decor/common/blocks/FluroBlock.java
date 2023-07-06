package com.grim3212.assorted.decor.common.blocks;

import com.google.common.collect.Maps;
import com.grim3212.assorted.decor.api.colorizer.ICanColor;
import net.minecraft.Util;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import java.util.Map;
import java.util.function.Supplier;

public class FluroBlock extends Block implements ICanColor {

    public static final Map<DyeColor, Supplier<FluroBlock>> FLURO_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), (map) -> {
        map.put(DyeColor.WHITE, () -> DecorBlocks.FLURO_WHITE.get());
        map.put(DyeColor.ORANGE, () -> DecorBlocks.FLURO_ORANGE.get());
        map.put(DyeColor.MAGENTA, () -> DecorBlocks.FLURO_MAGENTA.get());
        map.put(DyeColor.LIGHT_BLUE, () -> DecorBlocks.FLURO_LIGHT_BLUE.get());
        map.put(DyeColor.YELLOW, () -> DecorBlocks.FLURO_YELLOW.get());
        map.put(DyeColor.LIME, () -> DecorBlocks.FLURO_LIME.get());
        map.put(DyeColor.PINK, () -> DecorBlocks.FLURO_PINK.get());
        map.put(DyeColor.GRAY, () -> DecorBlocks.FLURO_GRAY.get());
        map.put(DyeColor.LIGHT_GRAY, () -> DecorBlocks.FLURO_LIGHT_GRAY.get());
        map.put(DyeColor.CYAN, () -> DecorBlocks.FLURO_CYAN.get());
        map.put(DyeColor.PURPLE, () -> DecorBlocks.FLURO_PURPLE.get());
        map.put(DyeColor.BLUE, () -> DecorBlocks.FLURO_BLUE.get());
        map.put(DyeColor.BROWN, () -> DecorBlocks.FLURO_BROWN.get());
        map.put(DyeColor.GREEN, () -> DecorBlocks.FLURO_GREEN.get());
        map.put(DyeColor.RED, () -> DecorBlocks.FLURO_RED.get());
        map.put(DyeColor.BLACK, () -> DecorBlocks.FLURO_BLACK.get());
    });

    private final DyeColor color;

    public FluroBlock(DyeColor color) {
        super(Block.Properties.of().mapColor(color).instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS).strength(0.2F, 1.0F).lightLevel(state -> 15).sound(SoundType.GLASS));
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    public DyeColor currentColor(BlockState state) {
        return color;
    }

    @Override
    public BlockState stateForColor(BlockState state, DyeColor color) {
        return FLURO_BY_DYE.get(color).get().defaultBlockState();
    }

}

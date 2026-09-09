package com.grim3212.assorted.decor.common.blocks;

import com.grim3212.assorted.decor.api.colorizer.ICanColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ColorChangingBlock extends Block implements ICanColor {

    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

    public ColorChangingBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, DyeColor.WHITE));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(COLOR);
    }

    @Override
    public DyeColor currentColor(BlockState state) {
        return state.getValue(COLOR);
    }

    @Override
    public BlockState stateForColor(BlockState state, DyeColor color) {
        return state.setValue(COLOR, color);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return getColorStack(new ItemStack(this), state.getValue(COLOR));
    }

    public static ItemStack getColorStack(ItemStack stack, DyeColor color) {
        ItemStack copy = stack.copy();
        // The BlockStateTag NBT is the BLOCK_STATE data component now
        copy.set(DataComponents.BLOCK_STATE, copy.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).with(COLOR, color));
        return copy;
    }

    /**
     * The same colour, as a component patch. Datagen cannot build ItemStacks - an item's default
     * components are only bound during a resource reload - so recipe results carry a patch instead.
     */
    public static DataComponentPatch getColorPatch(DyeColor color) {
        return DataComponentPatch.builder().set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(COLOR, color)).build();
    }
}

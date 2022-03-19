package com.grim3212.assorted.decor.common.block;

import java.util.Arrays;

import com.grim3212.assorted.decor.common.util.NBTHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.HitResult;

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
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
		Arrays.stream(DyeColor.values()).forEach(x -> items.add(getColorStack(new ItemStack(this), x)));
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
		return getColorStack(new ItemStack(this), state.getValue(COLOR));
	}

	public static ItemStack getColorStack(ItemStack stack, DyeColor color) {
		ItemStack copy = stack.copy();
		CompoundTag blockStateTag = new CompoundTag();
		NBTHelper.putString(blockStateTag, "color", color.getName());
		NBTHelper.putTag(copy, "BlockStateTag", blockStateTag);
		return copy;
	}
}

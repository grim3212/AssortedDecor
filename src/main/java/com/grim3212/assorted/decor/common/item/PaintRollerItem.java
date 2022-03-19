package com.grim3212.assorted.decor.common.item;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.grim3212.assorted.decor.common.block.ICanColor;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PaintRollerItem extends Item {

	public static final Map<DyeColor, Block> WOOL_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), (map) -> {
		map.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
		map.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
		map.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
		map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
		map.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
		map.put(DyeColor.LIME, Blocks.LIME_WOOL);
		map.put(DyeColor.PINK, Blocks.PINK_WOOL);
		map.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
		map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
		map.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
		map.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
		map.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
		map.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
		map.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
		map.put(DyeColor.RED, Blocks.RED_WOOL);
		map.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
	});

	public static final Map<DyeColor, Block> CONCRETE_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), (map) -> {
		map.put(DyeColor.WHITE, Blocks.WHITE_CONCRETE);
		map.put(DyeColor.ORANGE, Blocks.ORANGE_CONCRETE);
		map.put(DyeColor.MAGENTA, Blocks.MAGENTA_CONCRETE);
		map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CONCRETE);
		map.put(DyeColor.YELLOW, Blocks.YELLOW_CONCRETE);
		map.put(DyeColor.LIME, Blocks.LIME_CONCRETE);
		map.put(DyeColor.PINK, Blocks.PINK_CONCRETE);
		map.put(DyeColor.GRAY, Blocks.GRAY_CONCRETE);
		map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CONCRETE);
		map.put(DyeColor.CYAN, Blocks.CYAN_CONCRETE);
		map.put(DyeColor.PURPLE, Blocks.PURPLE_CONCRETE);
		map.put(DyeColor.BLUE, Blocks.BLUE_CONCRETE);
		map.put(DyeColor.BROWN, Blocks.BROWN_CONCRETE);
		map.put(DyeColor.GREEN, Blocks.GREEN_CONCRETE);
		map.put(DyeColor.RED, Blocks.RED_CONCRETE);
		map.put(DyeColor.BLACK, Blocks.BLACK_CONCRETE);
	});

	public static final Map<DyeColor, Block> CONCRETE_POWDER_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), (map) -> {
		map.put(DyeColor.WHITE, Blocks.WHITE_CONCRETE_POWDER);
		map.put(DyeColor.ORANGE, Blocks.ORANGE_CONCRETE_POWDER);
		map.put(DyeColor.MAGENTA, Blocks.MAGENTA_CONCRETE_POWDER);
		map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CONCRETE_POWDER);
		map.put(DyeColor.YELLOW, Blocks.YELLOW_CONCRETE_POWDER);
		map.put(DyeColor.LIME, Blocks.LIME_CONCRETE_POWDER);
		map.put(DyeColor.PINK, Blocks.PINK_CONCRETE_POWDER);
		map.put(DyeColor.GRAY, Blocks.GRAY_CONCRETE_POWDER);
		map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CONCRETE_POWDER);
		map.put(DyeColor.CYAN, Blocks.CYAN_CONCRETE_POWDER);
		map.put(DyeColor.PURPLE, Blocks.PURPLE_CONCRETE_POWDER);
		map.put(DyeColor.BLUE, Blocks.BLUE_CONCRETE_POWDER);
		map.put(DyeColor.BROWN, Blocks.BROWN_CONCRETE_POWDER);
		map.put(DyeColor.GREEN, Blocks.GREEN_CONCRETE_POWDER);
		map.put(DyeColor.RED, Blocks.RED_CONCRETE_POWDER);
		map.put(DyeColor.BLACK, Blocks.BLACK_CONCRETE_POWDER);
	});

	public static final Map<DyeColor, Block> CARPET_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), (map) -> {
		map.put(DyeColor.WHITE, Blocks.WHITE_CARPET);
		map.put(DyeColor.ORANGE, Blocks.ORANGE_CARPET);
		map.put(DyeColor.MAGENTA, Blocks.MAGENTA_CARPET);
		map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CARPET);
		map.put(DyeColor.YELLOW, Blocks.YELLOW_CARPET);
		map.put(DyeColor.LIME, Blocks.LIME_CARPET);
		map.put(DyeColor.PINK, Blocks.PINK_CARPET);
		map.put(DyeColor.GRAY, Blocks.GRAY_CARPET);
		map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CARPET);
		map.put(DyeColor.CYAN, Blocks.CYAN_CARPET);
		map.put(DyeColor.PURPLE, Blocks.PURPLE_CARPET);
		map.put(DyeColor.BLUE, Blocks.BLUE_CARPET);
		map.put(DyeColor.BROWN, Blocks.BROWN_CARPET);
		map.put(DyeColor.GREEN, Blocks.GREEN_CARPET);
		map.put(DyeColor.RED, Blocks.RED_CARPET);
		map.put(DyeColor.BLACK, Blocks.BLACK_CARPET);
	});

	public static final List<Map<DyeColor, Block>> BLOCKS_BY_DYE = Arrays.asList(WOOL_BY_DYE, CONCRETE_BY_DYE, CONCRETE_POWDER_BY_DYE, CARPET_BY_DYE);

	private final DyeColor dyeColor;

	public PaintRollerItem(DyeColor color, Properties props) {
		super(props.stacksTo(1).durability(64));
		this.dyeColor = color;
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		ItemStack copy = itemStack.copy();

		copy.setDamageValue(copy.getDamageValue() + 1);
		return copy.getDamageValue() >= copy.getMaxDamage() ? new ItemStack(DecorItems.PAINT_ROLLER.get()) : copy;
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
		if (entity instanceof Sheep) {
			Sheep sheep = (Sheep) entity;
			if (sheep.isAlive() && !sheep.isSheared() && sheep.getColor() != this.dyeColor) {
				sheep.level.playSound(player, sheep, SoundEvents.DYE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
				if (!player.level.isClientSide) {
					sheep.setColor(this.dyeColor);
					player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> {
						p.broadcastBreakEvent(hand);
					});
				}

				return InteractionResult.sidedSuccess(player.level.isClientSide);
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		Player player = context.getPlayer();
		InteractionHand hand = context.getHand();
		Block block = state.getBlock();

		if (block instanceof ICanColor canColor) {
			if (canColor.currentColor(state) != this.dyeColor) {
				player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> {
					p.broadcastBreakEvent(hand);
				});
				level.setBlock(pos, canColor.stateForColor(state, this.dyeColor), 3);
				return InteractionResult.sidedSuccess(level.isClientSide());
			}
		}

		Map<DyeColor, Block> match = isBlockFound(block);
		if (match != null) {
			Optional<DyeColor> curColor = match.entrySet().stream().filter(entry -> block.equals(entry.getValue())).map(Map.Entry::getKey).findFirst();
			if (curColor.isPresent() && curColor.get() != this.dyeColor) {
				player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> {
					p.broadcastBreakEvent(hand);
				});
				level.setBlock(pos, match.getOrDefault(this.dyeColor, block).defaultBlockState(), 3);
				return InteractionResult.sidedSuccess(level.isClientSide());
			}
		}

		return super.useOn(context);
	}

	@Nullable
	private Map<DyeColor, Block> isBlockFound(Block b) {
		Optional<Map<DyeColor, Block>> containsMatch = BLOCKS_BY_DYE.stream().filter((x) -> x.containsValue(b)).findFirst();
		if (containsMatch.isPresent()) {
			return containsMatch.get();
		}

		return null;
	}

	public DyeColor getDyeColor() {
		return this.dyeColor;
	}
}

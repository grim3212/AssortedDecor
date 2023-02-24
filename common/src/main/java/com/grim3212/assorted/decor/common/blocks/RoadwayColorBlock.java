package com.grim3212.assorted.decor.common.blocks;

import com.grim3212.assorted.decor.api.colorizer.ICanColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class RoadwayColorBlock extends Block implements ICanColor {

	private final DyeColor color;

	public RoadwayColorBlock(DyeColor color, Properties props) {
		super(props);
		this.color = color;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!player.getAbilities().mayBuild) {
			return InteractionResult.PASS;
		} else {
			if (player.getItemInHand(hand).is(Items.WATER_BUCKET)) {
				level.setBlock(pos, DecorBlocks.ROADWAY.get().defaultBlockState(), 3);
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
			return InteractionResult.PASS;
		}
	}

	@Override
	public DyeColor currentColor(BlockState state) {
		return this.color;
	}

	@Override
	public BlockState stateForColor(BlockState stat, DyeColor color) {
		return DecorBlocks.ROADWAY_COLORS.get(color).get().defaultBlockState();
	}
}

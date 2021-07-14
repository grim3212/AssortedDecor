package com.grim3212.assorted.decor.common.block.colorizer;

import com.grim3212.assorted.decor.AssortedDecor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ColorizerFireplaceBaseBlock extends ColorizerBlock {

	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public ColorizerFireplaceBaseBlock() {
		this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(ACTIVE);
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock() == this && world.getBlockState(pos).getValue(ACTIVE)) {
			return 15;
		}
		return super.getLightValue(state, world, pos);
	}

	@Override
	public void attack(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
		if (worldIn.getBlockState(pos).getValue(ACTIVE)) {
			if (!worldIn.isClientSide) {
				worldIn.setBlockAndUpdate(pos, worldIn.getBlockState(pos).setValue(ACTIVE, false));
			}
			AssortedDecor.proxy.produceSmoke(worldIn, pos, 0.5D, 0.5D, 0.5D, 3, true);
			worldIn.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, worldIn.random.nextFloat() * 0.4F + 0.8F);
		}
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		ItemStack heldItem = player.getItemInHand(hand);

		if (!heldItem.isEmpty() && (heldItem.getItem() == Items.FLINT_AND_STEEL || heldItem.getItem() == Items.FIRE_CHARGE)) {
			if (!worldIn.getBlockState(pos).getValue(ACTIVE)) {
				heldItem.hurtAndBreak(1, player, (ent) -> {
					ent.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
				});
				worldIn.playSound((PlayerEntity) null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, worldIn.random.nextFloat() * 0.4F + 0.8F);
				worldIn.setBlockAndUpdate(pos, state.setValue(ACTIVE, true));
			}

			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}
}

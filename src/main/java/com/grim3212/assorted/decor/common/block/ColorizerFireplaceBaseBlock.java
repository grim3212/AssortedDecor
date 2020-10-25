package com.grim3212.assorted.decor.common.block;

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
		this.setDefaultState(this.stateContainer.getBaseState().with(ACTIVE, false));
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(ACTIVE);
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock() == this && world.getBlockState(pos).get(ACTIVE)) {
			return 15;
		}
		return super.getLightValue(state, world, pos);
	}

	@Override
	public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
		if (worldIn.getBlockState(pos).get(ACTIVE)) {
			if (!worldIn.isRemote) {
				AssortedDecor.proxy.produceSmoke(worldIn, pos, 0.5D, 0.5D, 0.5D, 3, true);
				worldIn.setBlockState(pos, worldIn.getBlockState(pos).with(ACTIVE, false));
			}
			worldIn.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.4F + 0.8F);
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		ItemStack heldItem = player.getHeldItem(hand);

		if (worldIn.isRemote)
			return ActionResultType.PASS;

		if (!heldItem.isEmpty() && (heldItem.getItem() == Items.FLINT_AND_STEEL || heldItem.getItem() == Items.FIRE_CHARGE)) {
			if (!worldIn.getBlockState(pos).get(ACTIVE)) {
				heldItem.damageItem(1, player, (ent) -> {
					ent.sendBreakAnimation(EquipmentSlotType.MAINHAND);
				});
				worldIn.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.4F + 0.8F);
				worldIn.setBlockState(pos, state.with(ACTIVE, true));
			}

			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}
}

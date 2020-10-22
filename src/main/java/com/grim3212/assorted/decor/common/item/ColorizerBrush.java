package com.grim3212.assorted.decor.common.item;

import java.util.List;

import com.grim3212.assorted.decor.common.block.IColorizer;
import com.grim3212.assorted.decor.common.handler.DecorConfig;
import com.grim3212.assorted.decor.common.util.NBTHelper;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ColorizerBrush extends Item {

	public ColorizerBrush(Properties properties) {
		super(properties.maxDamage(DecorConfig.COMMON.brushChargeCount.get()));
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		BlockState state = NBTUtil.readBlockState(NBTHelper.getTag(stack, "stored_state"));

		if (state.getBlock() == Blocks.AIR) {
			tooltip.add(new TranslationTextComponent("tooltip.colorizer_brush.empty"));
		} else {
			tooltip.add(new TranslationTextComponent("tooltip.colorizer_brush.stored", state.getBlock().getTranslatedName()));
		}

	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		PlayerEntity player = context.getPlayer();
		Hand hand = context.getHand();

		BlockState stored = NBTUtil.readBlockState(NBTHelper.getTag(stack, "stored_state"));
		BlockState hit = world.getBlockState(context.getPos());

		if (stored.getBlock() == Blocks.AIR) {
			// TODO: Check to make sure it is an allowed block

			if (hit.getBlock() != null && hit.getBlock() != Blocks.AIR && !(hit.getBlock() instanceof IColorizer)) {
				NBTHelper.putTag(stack, "stored_state", NBTUtil.writeBlockState(hit));
				// Reset damage after grabbing a new block
				stack.setDamage(0);
				world.setBlockState(pos, Blocks.AIR.getDefaultState());

				player.swingArm(hand);
			}
		} else {
			if (hit.getBlock() != null && hit.getBlock() != Blocks.AIR && hit.getBlock() instanceof IColorizer) {
				IColorizer colorizerBlock = (IColorizer) hit.getBlock();

				if (colorizerBlock.getStoredState(world, pos) == stored) {
					return ActionResultType.PASS;
				}

				colorizerBlock.setColorizer(world, pos, stored, player, hand, false);

				SoundType placeSound = stored.getSoundType(world, pos, player);
				world.playSound(player, pos, placeSound.getPlaceSound(), SoundCategory.BLOCKS, (placeSound.getVolume() + 1.0F) / 2.0F, placeSound.getPitch() * 0.8F);
				player.swingArm(hand);

				int dmg = stack.getDamage() + 1;
				if (stack.getMaxDamage() - dmg <= 0) {
					player.setHeldItem(hand, new ItemStack(DecorItems.COLORIZER_BRUSH.get()));
				} else {
					stack.setDamage(dmg);
				}

				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}
}

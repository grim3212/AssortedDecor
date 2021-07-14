package com.grim3212.assorted.decor.common.item;

import java.util.List;

import com.grim3212.assorted.decor.common.block.IColorizer;
import com.grim3212.assorted.decor.common.handler.DecorConfig;
import com.grim3212.assorted.decor.common.util.NBTHelper;

import net.minecraft.block.Block;
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
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ColorizerBrush extends Item {

	public ColorizerBrush(Properties properties) {
		super(properties.durability(DecorConfig.COMMON.brushChargeCount.get()));
	}

	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		BlockState state = NBTUtil.readBlockState(NBTHelper.getTag(stack, "stored_state"));

		if (state.getBlock() == Blocks.AIR) {
			tooltip.add(new TranslationTextComponent("tooltip.colorizer_brush.empty"));
		} else {
			tooltip.add(new TranslationTextComponent("tooltip.colorizer_brush.stored", state.getBlock().getName()));
		}
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		PlayerEntity player = context.getPlayer();
		Hand hand = context.getHand();

		BlockState stored = NBTUtil.readBlockState(NBTHelper.getTag(stack, "stored_state"));
		BlockState hit = world.getBlockState(context.getClickedPos());

		if (stored.getBlock() == Blocks.AIR || (player.isCrouching() && player.isCreative())) {
			if (colorizerAccepted(world, pos, hit)) {
				
				if (DecorConfig.COMMON.consumeBlock.get()) {
					if ((hit.getDestroyProgress(player, world, pos) != 0.0F || player.isCreative()) && !DecorConfig.COMMON.brushDisallowedBlockStates.getLoadedStates().contains(hit))
						world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
					else
						return ActionResultType.PASS;
				}
				
				NBTHelper.putTag(stack, "stored_state", NBTUtil.writeBlockState(hit));
				// Reset damage after grabbing a new block
				stack.setDamageValue(0);
				player.swing(hand);
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
				player.swing(hand);

				if (!player.isCreative()) {
					int dmg = stack.getDamageValue() + 1;
					if (stack.getMaxDamage() - dmg <= 0) {
						player.setItemInHand(hand, new ItemStack(DecorItems.COLORIZER_BRUSH.get()));
					} else {
						stack.setDamageValue(dmg);
					}
				}

				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	protected boolean colorizerAccepted(IBlockReader world, BlockPos pos, BlockState state) {
		Block block = state.getBlock();

		if (block != null && block != Blocks.AIR && !(block instanceof IColorizer)) {
			return isShapeFullCube(state.getShape(world, pos), state);
		}
		return false;
	}

	public boolean isShapeFullCube(VoxelShape shape, BlockState state) {
		return Block.isFaceFull(shape, Direction.UP) && Block.isFaceFull(shape, Direction.DOWN) && Block.isFaceFull(shape, Direction.EAST) && Block.isFaceFull(shape, Direction.WEST) && Block.isFaceFull(shape, Direction.NORTH) && Block.isFaceFull(shape, Direction.SOUTH);
	}
}

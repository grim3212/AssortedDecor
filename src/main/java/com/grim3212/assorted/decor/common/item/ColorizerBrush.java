package com.grim3212.assorted.decor.common.item;

import java.util.List;

import com.grim3212.assorted.decor.common.block.IColorizer;
import com.grim3212.assorted.decor.common.handler.DecorConfig;
import com.grim3212.assorted.decor.common.util.NBTHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ColorizerBrush extends Item {

	public ColorizerBrush(Properties properties) {
		super(properties.durability(16));
	}

	@Override
	public boolean isDamageable(ItemStack stack) {
		return true;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return DecorConfig.COMMON.brushChargeCount.get();
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		BlockState state = NbtUtils.readBlockState(NBTHelper.getTag(stack, "stored_state"));

		if (state.getBlock() == Blocks.AIR) {
			tooltip.add(Component.translatable("tooltip.colorizer_brush.empty"));
		} else {
			tooltip.add(Component.translatable("tooltip.colorizer_brush.stored", state.getBlock().getName()));
		}
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		InteractionHand hand = context.getHand();

		BlockState stored = NbtUtils.readBlockState(NBTHelper.getTag(stack, "stored_state"));
		BlockState hit = world.getBlockState(context.getClickedPos());

		if (stored.getBlock() == Blocks.AIR || (player.isCrouching() && player.isCreative())) {
			if (colorizerAccepted(world, pos, hit)) {

				if (DecorConfig.COMMON.consumeBlock.get()) {
					if ((hit.getDestroyProgress(player, world, pos) != 0.0F || player.isCreative()) && !DecorConfig.COMMON.brushDisallowedBlockStates.getLoadedStates().contains(hit))
						world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
					else
						return InteractionResult.PASS;
				}

				NBTHelper.putTag(stack, "stored_state", NbtUtils.writeBlockState(hit));
				// Reset damage after grabbing a new block
				stack.setDamageValue(0);
				player.swing(hand);
			}
		} else {
			if (hit.getBlock() != null && hit.getBlock() != Blocks.AIR && hit.getBlock() instanceof IColorizer) {
				IColorizer colorizerBlock = (IColorizer) hit.getBlock();

				if (colorizerBlock.getStoredState(world, pos) == stored) {
					return InteractionResult.PASS;
				}

				colorizerBlock.setColorizer(world, pos, stored, player, hand, false);

				SoundType placeSound = stored.getSoundType(world, pos, player);
				world.playSound(player, pos, placeSound.getPlaceSound(), SoundSource.BLOCKS, (placeSound.getVolume() + 1.0F) / 2.0F, placeSound.getPitch() * 0.8F);
				player.swing(hand);

				if (!player.isCreative()) {
					int dmg = stack.getDamageValue() + 1;
					if (stack.getMaxDamage() - dmg <= 0) {
						player.setItemInHand(hand, new ItemStack(DecorItems.COLORIZER_BRUSH.get()));
					} else {
						stack.setDamageValue(dmg);
					}
				}

				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	protected boolean colorizerAccepted(BlockGetter world, BlockPos pos, BlockState state) {
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

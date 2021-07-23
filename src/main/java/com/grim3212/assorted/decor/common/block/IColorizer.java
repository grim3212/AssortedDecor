package com.grim3212.assorted.decor.common.block;

import javax.annotation.Nullable;

import com.grim3212.assorted.decor.common.block.tileentity.ColorizerTileEntity;
import com.grim3212.assorted.decor.common.handler.DecorConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;

public interface IColorizer {

	public default boolean clearColorizer(Level worldIn, BlockPos pos, Player player, InteractionHand hand) {
		BlockState state = worldIn.getBlockState(pos);
		BlockEntity te = worldIn.getBlockEntity(pos);
		if (te instanceof ColorizerTileEntity) {
			ColorizerTileEntity tileColorizer = (ColorizerTileEntity) te;
			BlockState storedState = tileColorizer.getStoredBlockState();

			// Can only clear a filled colorizer
			if (storedState != Blocks.AIR.defaultBlockState()) {

				if (DecorConfig.COMMON.consumeBlock.get() && !player.getAbilities().instabuild) {
					ItemEntity blockDropped = new ItemEntity(worldIn, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), new ItemStack(tileColorizer.getStoredBlockState().getBlock(), 1));
					if (!worldIn.isClientSide) {
						worldIn.addFreshEntity(blockDropped);
						if (!(player instanceof FakePlayer)) {
							blockDropped.playerTouch(player);
						}
					}
				}

				// Clear Self
				if (setColorizer(worldIn, pos, null, player, hand, false)) {
					SoundType placeSound = state.getSoundType(worldIn, pos, player);

					worldIn.playSound(player, pos, placeSound.getPlaceSound(), SoundSource.BLOCKS, (placeSound.getVolume() + 1.0F) / 2.0F, placeSound.getPitch() * 0.8F);
					return true;
				}
			}
		}
		return false;
	}

	public default boolean setColorizer(Level worldIn, BlockPos pos, @Nullable BlockState toSetState, Player player, InteractionHand hand, boolean consumeItem) {
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
		if (tileentity instanceof ColorizerTileEntity) {
			ColorizerTileEntity te = (ColorizerTileEntity) tileentity;
			te.setStoredBlockState(toSetState != null ? toSetState : Blocks.AIR.defaultBlockState());

			// Remove an item if config allows and we are not resetting
			// colorizer
			if (DecorConfig.COMMON.consumeBlock.get() && toSetState != null && consumeItem) {
				if (!player.getAbilities().instabuild)
					player.getItemInHand(hand).shrink(1);
			}

			return true;
		}
		return false;
	}

	public default BlockState getStoredState(BlockGetter worldIn, BlockPos pos) {
		BlockEntity te = worldIn.getBlockEntity(pos);
		if (te instanceof ColorizerTileEntity) {
			return ((ColorizerTileEntity) te).getStoredBlockState();
		}
		return Blocks.AIR.defaultBlockState();
	}
}

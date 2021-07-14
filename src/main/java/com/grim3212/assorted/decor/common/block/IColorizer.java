package com.grim3212.assorted.decor.common.block;

import javax.annotation.Nullable;

import com.grim3212.assorted.decor.common.block.tileentity.ColorizerTileEntity;
import com.grim3212.assorted.decor.common.handler.DecorConfig;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public interface IColorizer {

	public default boolean clearColorizer(World worldIn, BlockPos pos, PlayerEntity player, Hand hand) {
		BlockState state = worldIn.getBlockState(pos);
		TileEntity te = worldIn.getBlockEntity(pos);
		if (te instanceof ColorizerTileEntity) {
			ColorizerTileEntity tileColorizer = (ColorizerTileEntity) te;
			BlockState storedState = tileColorizer.getStoredBlockState();

			// Can only clear a filled colorizer
			if (storedState != Blocks.AIR.defaultBlockState()) {

				if (DecorConfig.COMMON.consumeBlock.get() && !player.abilities.instabuild) {
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

					worldIn.playSound(player, pos, placeSound.getPlaceSound(), SoundCategory.BLOCKS, (placeSound.getVolume() + 1.0F) / 2.0F, placeSound.getPitch() * 0.8F);
					return true;
				}
			}
		}
		return false;
	}

	public default boolean setColorizer(World worldIn, BlockPos pos, @Nullable BlockState toSetState, PlayerEntity player, Hand hand, boolean consumeItem) {
		TileEntity tileentity = worldIn.getBlockEntity(pos);
		if (tileentity instanceof ColorizerTileEntity) {
			ColorizerTileEntity te = (ColorizerTileEntity) tileentity;
			te.setStoredBlockState(toSetState != null ? toSetState : Blocks.AIR.defaultBlockState());

			// Remove an item if config allows and we are not resetting
			// colorizer
			if (DecorConfig.COMMON.consumeBlock.get() && toSetState != null && consumeItem) {
				if (!player.abilities.instabuild)
					player.getItemInHand(hand).shrink(1);
			}

			return true;
		}
		return false;
	}

	public default BlockState getStoredState(IBlockReader worldIn, BlockPos pos) {
		TileEntity te = worldIn.getBlockEntity(pos);
		if (te instanceof ColorizerTileEntity) {
			return ((ColorizerTileEntity) te).getStoredBlockState();
		}
		return Blocks.AIR.defaultBlockState();
	}
}

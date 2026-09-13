package com.grim3212.assorted.decor.api.colorizer;

import com.grim3212.assorted.decor.DecorCommonMod;
import com.grim3212.assorted.decor.client.model.ColorizerClientEffects;
import com.grim3212.assorted.decor.common.blocks.blockentity.ColorizerBlockEntity;
import com.grim3212.assorted.lib.core.block.*;
import com.grim3212.assorted.lib.core.block.effects.IBlockClientEffects;
import com.grim3212.assorted.lib.core.block.effects.IBlockEffectSupplier;
import com.grim3212.assorted.lib.core.block.effects.IBlockLandingEffects;
import com.grim3212.assorted.lib.core.block.effects.IBlockRunningEffects;
import com.grim3212.assorted.lib.platform.Services;
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
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A colorizer stands in for the block it stores: its light, sound, friction, harvest behaviour and
 * effects all come from that block rather than from the colorizer itself. Light dampening is the
 * exception - vanilla bakes it into the block state, so the full cubes carry their stored block's in
 * {@code ColorizerFullCubeBlock#LIGHT_DAMPENING}. Only they take it: a stairs or a fence holding
 * stone is still mostly air.
 */
public interface IColorizer extends IBlockExtraProperties, IBlockSoundType, IBlockLightEmission, IBlockLightDampening, IBlockCanHarvest, IBlockLandingEffects, IBlockRunningEffects, IBlockEffectSupplier {

    /** A colorizer's own dampening. The full cubes override this to take their stored block's. */
    @Override
    default int getLightDampening(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return state.getLightDampening();
    }

    /**
     * Every shape lets skylight through when its stored block would, which is what the library
     * reports for it; the light engines themselves decide from {@link #getLightDampening}.
     */
    @Override
    default boolean propagatesSkylightDown(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        final BlockState stored = getStoredState(blockGetter, pos);
        return stored.isAir() ? state.propagatesSkylightDown() : stored.propagatesSkylightDown();
    }

    default boolean clearColorizer(Level worldIn, BlockPos pos, Player player, InteractionHand hand) {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof ColorizerBlockEntity) {
            ColorizerBlockEntity tileColorizer = (ColorizerBlockEntity) te;
            BlockState storedState = tileColorizer.getStoredBlockState();

            // Can only clear a filled colorizer
            if (storedState != Blocks.AIR.defaultBlockState()) {

                if (DecorCommonMod.COMMON_CONFIG.colorizerConsumeBlock.get() && !player.getAbilities().instabuild) {
                    ItemEntity blockDropped = new ItemEntity(worldIn, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), new ItemStack(tileColorizer.getStoredBlockState().getBlock(), 1));
                    if (!worldIn.isClientSide()) {
                        worldIn.addFreshEntity(blockDropped);
                        if (!Services.PLATFORM.isFakePlayer(player)) {
                            blockDropped.playerTouch(player);
                        }
                    }
                }

                // Clear Self
                if (setColorizer(worldIn, pos, null, player, hand, false)) {
                    SoundType placeSound = Services.LEVEL_PROPERTIES.getSoundType(worldIn, pos, player);

                    worldIn.playSound(player, pos, placeSound.getPlaceSound(), SoundSource.BLOCKS, (placeSound.getVolume() + 1.0F) / 2.0F, placeSound.getPitch() * 0.8F);
                    return true;
                }
            }
        }
        return false;
    }

    default boolean setColorizer(Level worldIn, BlockPos pos, @Nullable BlockState toSetState, Player player, InteractionHand hand, boolean consumeItem) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof ColorizerBlockEntity) {
            ColorizerBlockEntity te = (ColorizerBlockEntity) tileentity;
            te.setStoredBlockState(toSetState != null ? toSetState : Blocks.AIR.defaultBlockState());

            // Remove an item if config allows and we are not resetting
            // colorizer
            if (DecorCommonMod.COMMON_CONFIG.colorizerConsumeBlock.get() && toSetState != null && consumeItem) {
                if (!player.getAbilities().instabuild)
                    player.getItemInHand(hand).shrink(1);
            }

            return true;
        }
        return false;
    }

    /**
     * Gives the colorizer at {@code to} the block stored at {@code from}. For a block that places its
     * other parts itself in {@code setPlacedBy}, after the part the item placed has taken the item's
     * stored block.
     */
    default void copyStoredState(Level level, BlockPos from, BlockPos to) {
        if (level.getBlockEntity(from) instanceof ColorizerBlockEntity source && level.getBlockEntity(to) instanceof ColorizerBlockEntity target) {
            target.setStoredBlockState(source.getStoredBlockState());
        }
    }

    /**
     * Every shape emits the light of its stored block. Read once: the light engines ask this per
     * node, from their own thread. A class that extends {@code ExtraPropertyBlock} inherits its
     * implementation instead and delegates here itself.
     */
    @Override
    default int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        final BlockState stored = getStoredState(world, pos);
        return stored.isAir() ? state.getLightEmission() : stored.getLightEmission();
    }

    /** Read the way the light engine reads it - from any thread - since that is who asks for the emission. */
    default BlockState getStoredState(BlockGetter worldIn, BlockPos pos) {
        if (IBlockLightEmission.blockEntityAt(worldIn, pos) instanceof ColorizerBlockEntity colorizer) {
            return colorizer.getStoredBlockState();
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    default Supplier<IBlockClientEffects> getClientEffects() {
        return ColorizerClientEffects::new;
    }

}

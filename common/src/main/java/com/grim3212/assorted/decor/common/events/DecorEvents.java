package com.grim3212.assorted.decor.common.events;

import com.grim3212.assorted.decor.DecorConfig;
import com.grim3212.assorted.decor.api.DecorTags;
import com.grim3212.assorted.decor.api.colorizer.IColorizer;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.lib.events.UseBlockEvent;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DecorEvents {
    public static void init() {
        Services.EVENTS.registerEvent(UseBlockEvent.class, (final UseBlockEvent event) -> {
            InteractionResult result = useOnBlock(event.getPlayer(), event.getLevel(), event.getHand(), event.getHitResult());
            if (result == InteractionResult.SUCCESS) {
                event.setCanceled(true);
                event.setResult(result);
            }
        });
    }

    private static InteractionResult useOnBlock(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isSpectator()) {
            return InteractionResult.PASS;
        }

        ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() != DecorItems.COLORIZER_BRUSH.get()) {
            return InteractionResult.PASS;
        }

        BlockPos pos = hitResult.getBlockPos();
        BlockState stored = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), NBTHelper.getTag(stack, "stored_state"));
        BlockState hit = world.getBlockState(pos);

        if (stored.getBlock() == Blocks.AIR || (player.isCrouching() && player.isCreative())) {
            if (colorizerAccepted(world, pos, hit)) {
                if (DecorConfig.Common.colorizerConsumeBlock.getValue()) {
                    Block block = hit.getBlock();
                    if ((hit.getDestroyProgress(player, world, pos) != 0.0F || player.isCreative()) && !hit.is(DecorTags.Blocks.BRUSH_DISALLOWED_BLOCKS))
                        world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    else
                        return InteractionResult.PASS;
                }

                NBTHelper.putTag(stack, "stored_state", NbtUtils.writeBlockState(hit));
                // Reset damage after grabbing a new block
                stack.setDamageValue(0);
                player.swing(hand);
                return InteractionResult.SUCCESS;
            }
        } else {
            if (hit.getBlock() != null && hit.getBlock() != Blocks.AIR && hit.getBlock() instanceof IColorizer) {
                IColorizer colorizerBlock = (IColorizer) hit.getBlock();

                if (colorizerBlock.getStoredState(world, pos) == stored) {
                    return InteractionResult.PASS;
                }

                colorizerBlock.setColorizer(world, pos, stored, player, hand, false);

                SoundType placeSound = Services.PLATFORM.getSoundType(stored, world, pos, player);
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

    private static boolean colorizerAccepted(BlockGetter world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();

        if (block != null && block != Blocks.AIR && !(block instanceof IColorizer)) {
            return isShapeFullCube(state.getShape(world, pos), state);
        }
        return false;
    }

    private static boolean isShapeFullCube(VoxelShape shape, BlockState state) {
        return Block.isFaceFull(shape, Direction.UP) && Block.isFaceFull(shape, Direction.DOWN) && Block.isFaceFull(shape, Direction.EAST) && Block.isFaceFull(shape, Direction.WEST) && Block.isFaceFull(shape, Direction.NORTH) && Block.isFaceFull(shape, Direction.SOUTH);
    }
}

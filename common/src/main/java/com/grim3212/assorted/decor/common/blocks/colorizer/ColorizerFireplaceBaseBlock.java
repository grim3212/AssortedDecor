package com.grim3212.assorted.decor.common.blocks.colorizer;


import com.grim3212.assorted.decor.api.util.DecorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

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
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() == this && world.getBlockState(pos).getValue(ACTIVE)) {
            return 15;
        }
        return super.getLightEmission(state, world, pos);
    }

    @Override
    public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) {
        if (worldIn.getBlockState(pos).getValue(ACTIVE)) {
            if (!worldIn.isClientSide) {
                worldIn.setBlockAndUpdate(pos, worldIn.getBlockState(pos).setValue(ACTIVE, false));
            }
            DecorUtil.produceSmoke(worldIn, pos, 0.5D, 0.5D, 0.5D, 3, true);
            worldIn.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, worldIn.random.nextFloat() * 0.4F + 0.8F);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (!heldItem.isEmpty() && (heldItem.getItem() == Items.FLINT_AND_STEEL || heldItem.getItem() == Items.FIRE_CHARGE)) {
            if (!worldIn.getBlockState(pos).getValue(ACTIVE)) {
                heldItem.hurtAndBreak(1, player, (ent) -> {
                    ent.broadcastBreakEvent(EquipmentSlot.MAINHAND);
                });
                worldIn.playSound((Player) null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, worldIn.random.nextFloat() * 0.4F + 0.8F);
                worldIn.setBlockAndUpdate(pos, state.setValue(ACTIVE, true));
            }

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}

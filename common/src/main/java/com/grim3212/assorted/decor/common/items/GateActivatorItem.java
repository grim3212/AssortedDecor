package com.grim3212.assorted.decor.common.items;

import com.grim3212.assorted.decor.common.blocks.GateBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

/**
 * The gate trumpet and garage remote. Right clicking the gate works the way any block does; used in
 * the air it works the first of its gates straight ahead of the player, within {@link #RANGE}
 * blocks and a few blocks above or below.
 */
public class GateActivatorItem extends Item {

    public static final int RANGE = 32;
    private static final int ABOVE = 3;
    private static final int BELOW = 2;

    private final Supplier<? extends Block> gate;
    private final Supplier<SoundEvent> sound;
    /** Ticks before it can be used again: the length of its sound, so blasts never overlap. */
    private final int cooldown;

    public GateActivatorItem(Supplier<? extends Block> gate, Supplier<SoundEvent> sound, int cooldown, Properties properties) {
        super(properties);
        this.gate = gate;
        this.sound = sound;
        this.cooldown = cooldown;
    }

    /** Sounds it, whether or not there is a gate to answer, and starts its cooldown. */
    public void sound(Level level, Player player, ItemStack stack) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(), this.sound.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        player.getCooldowns().addCooldown(stack, this.cooldown);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            this.sound(level, player, stack);
            BlockPos found = this.find(level, player.blockPosition(), player.getDirection());
            if (found != null && level.getBlockState(found).getBlock() instanceof GateBlock gateBlock) {
                gateBlock.setOpen(player, level, found, !level.getBlockState(found).getValue(GateBlock.OPEN));
            }
        }
        return InteractionResult.SUCCESS;
    }

    private BlockPos find(Level level, BlockPos from, Direction facing) {
        for (int distance = 0; distance < RANGE; distance++) {
            BlockPos along = from.relative(facing, distance);
            for (int dy = ABOVE; dy >= -BELOW; dy--) {
                BlockPos pos = along.above(dy);
                BlockState state = level.getBlockState(pos);
                if (state.is(this.gate.get())) {
                    return pos;
                }
            }
        }
        return null;
    }
}

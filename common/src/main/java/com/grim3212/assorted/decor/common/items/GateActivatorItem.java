package com.grim3212.assorted.decor.common.items;

import com.grim3212.assorted.decor.common.blocks.GateBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * The gate trumpet and garage remote. Right clicking the gate works the way any block does; used in
 * the air it works the nearest of its gates along where the player is looking, within
 * {@link #RANGE} blocks.
 */
public class GateActivatorItem extends Item {

    public static final int RANGE = 32;
    /** How far along the look to step between checks; half a block misses no block the look crosses. */
    private static final double STEP = 0.5D;

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
            BlockPos found = this.find(level, player.getEyePosition(), player.getViewVector(1.0F));
            if (found != null && level.getBlockState(found).getBlock() instanceof GateBlock gateBlock) {
                gateBlock.setOpen(player, level, found, !level.getBlockState(found).getValue(GateBlock.OPEN));
            }
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * The nearest of its gates along the look from {@code eye}. Each step checks the block the look
     * is in and the ones around it, so the gate does not have to be aimed at exactly, and an open
     * gate is found by its empty doorway as well as by its top.
     */
    private @Nullable BlockPos find(Level level, Vec3 eye, Vec3 look) {
        for (double distance = STEP; distance <= RANGE; distance += STEP) {
            BlockPos center = BlockPos.containing(eye.add(look.scale(distance)));
            for (BlockPos pos : BlockPos.withinManhattan(center, 1, 1, 1)) {
                if (level.getBlockState(pos).is(this.gate.get())) {
                    return pos.immutable();
                }
            }

            BlockPos doorway = this.openGateOver(level, center);
            if (doorway != null) {
                return doorway;
            }
        }
        return null;
    }

    /** The top of an open gate whose doorway {@code pos} is in, if there is one. */
    private @Nullable BlockPos openGateOver(Level level, BlockPos pos) {
        if (!level.getBlockState(pos).isAir()) {
            return null;
        }
        for (BlockPos p = pos.above(); p.getY() - pos.getY() <= GateBlock.MAX_LENGTH; p = p.above()) {
            BlockState state = level.getBlockState(p);
            if (state.isAir()) {
                continue;
            }
            if (state.is(this.gate.get()) && state.getBlock() instanceof GateBlock gateBlock && state.getValue(GateBlock.OPEN)
                    && gateBlock.reach(level, p).getLast().getY() <= pos.getY()) {
                return p;
            }
            return null;
        }
        return null;
    }
}

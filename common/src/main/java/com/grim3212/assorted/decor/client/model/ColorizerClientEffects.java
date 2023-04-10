package com.grim3212.assorted.decor.client.model;

import com.grim3212.assorted.decor.api.colorizer.IColorizer;
import com.grim3212.assorted.lib.core.block.effects.EffectUtils;
import com.grim3212.assorted.lib.core.block.effects.IBlockClientEffects;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ColorizerClientEffects implements IBlockClientEffects {

    @Override
    public boolean addHitEffects(BlockState state, Level level, BlockPos pos, Direction dir, ParticleEngine manager) {
        if (state.getBlock() instanceof IColorizer colorizer) {
            BlockState stored = colorizer.getStoredState(level, pos);
            if (!stored.isAir()) {
                return EffectUtils.addHitEffects(level, pos, dir, stored, manager);
            }
        }

        return false;
    }

    @Override
    public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
        if (state.getBlock() instanceof IColorizer colorizer) {
            BlockState stored = colorizer.getStoredState(level, pos);
            if (!stored.isAir()) {
                EffectUtils.addBlockDestroyEffects(level, pos, stored, manager, level);
                return true;
            }
        }

        return false;
    }
}

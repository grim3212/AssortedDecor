package com.grim3212.assorted.decor.common.blocks.blockentity;

import com.grim3212.assorted.decor.common.blocks.WallClockBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WallClockBlockEntity extends BlockEntity {

    public WallClockBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public WallClockBlockEntity(BlockPos pos, BlockState state) {
        super(DecorBlockEntityTypes.WALL_CLOCK.get(), pos, state);
    }

    private int time = 0;

    public int getTime() {
        return time;
    }

    private double field_94239_h;
    private double field_94240_i;

    public void tick() {
        double d0 = 0.0D;

        if (getLevel() != null) {
            float f = getLevel().getTimeOfDay(1.0F);
            d0 = (double) f;

            if (getLevel().dimensionType().hasFixedTime()) {
                d0 = Math.random();
            }
        }

        double d1;

        for (d1 = d0 - field_94239_h; d1 < -0.5D; ++d1) {
            ;
        }

        while (d1 >= 0.5D) {
            --d1;
        }

        d1 = Mth.clamp(d1, -1.0D, 1.0D);
        field_94240_i += d1 * 0.1D;
        field_94240_i *= 0.8D;
        field_94239_h += field_94240_i;

        int i;
        int numFrames = 64;
        for (i = (int) ((this.field_94239_h + 1.0D) * (double) numFrames) % numFrames; i < 0; i = (i + numFrames) % numFrames) {
            ;
        }
        if (i != time) {
            time = i;
            BlockState state = this.getBlockState();
            this.getLevel().setBlockAndUpdate(getBlockPos(), state.setValue(WallClockBlock.TIME, getTime()));
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}

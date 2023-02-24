package com.grim3212.assorted.decor.common.entity;

import com.grim3212.assorted.decor.common.items.FrameItem.FrameMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class IronFrameEntity extends FrameEntity {

    public IronFrameEntity(EntityType<? extends FrameEntity> type, Level world) {
        super(type, world);
    }

    public IronFrameEntity(Level world, BlockPos pos, Direction direction) {
        super(DecorEntityTypes.IRON_FRAME.get(), world, pos, direction);
    }

    @Override
    public FrameMaterial getFrameMaterial() {
        return FrameMaterial.IRON;
    }
}

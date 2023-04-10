package com.grim3212.assorted.decor.common.helpers;

import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.blockentity.CageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class CageLogic {

    private final CageBlockEntity cage;
    /**
     * The delay to spawn.
     */
    private int spawnDelay = 400;
    /**
     * The rotation of the mob inside the mob spawner
     */
    private double mobRotation;
    /**
     * the previous rotation of the mob inside the mob spawner
     */
    private double prevMobRotation;
    private int activatingRangeFromPlayer = 16;

    public CageLogic(CageBlockEntity cage) {
        this.cage = cage;
    }

    public void broadcastEvent(int id) {
        cage.getLevel().blockEvent(cage.getBlockPos(), DecorBlocks.CAGE.get(), id, 0);
    }

    public Level getSpawnerWorld() {
        return cage.getLevel();
    }

    public BlockPos getSpawnerPosition() {
        return cage.getBlockPos();
    }

    public Entity getEntity() {
        return cage.getCachedEntity();
    }

    /**
     * Returns true if there's a player close enough to this mob spawner to activate
     * it.
     */
    private boolean isActivated() {
        BlockPos blockpos = this.getSpawnerPosition();
        return this.getSpawnerWorld().hasNearbyAlivePlayer((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.5D, (double) blockpos.getZ() + 0.5D, (double) this.activatingRangeFromPlayer);
    }

    public void clientTick() {
        if (!this.isActivated()) {
            this.prevMobRotation = this.mobRotation;
        } else {
            this.prevMobRotation = this.mobRotation;
            this.mobRotation = (this.mobRotation + (double) (1000.0F / ((float) this.spawnDelay + 200.0F))) % 360.0D;
        }
    }

    public void serverTick() {
//        if (this.isActivated()) {
//            if (this.getEntity() != null) {
//                if (this.getEntity() instanceof Mob mob) {
//                    mob.baseTick();
//                }
//            }
//        }
    }

    public double getMobRotation() {
        return this.mobRotation;
    }

    public double getPrevMobRotation() {
        return this.prevMobRotation;
    }
}

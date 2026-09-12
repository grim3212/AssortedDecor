package com.grim3212.assorted.decor.common.blocks.colorizer;

import com.grim3212.assorted.decor.api.colorizer.IColorizer;
import com.grim3212.assorted.decor.common.blocks.blockentity.ColorizerBlockEntity;
import com.grim3212.assorted.lib.core.block.effects.ServerEffectUtils;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ColorizerDoorBlock extends DoorBlock implements IColorizer, EntityBlock {

    public ColorizerDoorBlock(Properties props) {
        super(BlockSetType.POLISHED_BLACKSTONE, props);
    }

    // IColorizer#clearColorizer clears through this as well, so it empties every part and needs no override.
    @Override
    public boolean setColorizer(Level worldIn, BlockPos pos, BlockState toSetState, Player player, InteractionHand hand, boolean consumeItem) {
        if (IColorizer.super.setColorizer(worldIn, pos, toSetState, player, hand, consumeItem)) {
            BlockState state = worldIn.getBlockState(pos);

            if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
                return IColorizer.super.setColorizer(worldIn, pos.above(), toSetState, player, hand, consumeItem);
            }
            return IColorizer.super.setColorizer(worldIn, pos.below(), toSetState, player, hand, consumeItem);
        }

        return false;
    }

    /**
     * Vanilla places the upper half here, after the lower half has taken the stored block of the item
     * it was placed from, so the upper half is given the same block.
     */
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        this.copyStoredState(level, pos, pos.above());
    }

    /// ===============================================
    /// ======== DEFAULT COLORIZER STUFF BELOW ========
    /// ===============================================
    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        BlockState stored = this.getStoredState(reader, pos);
        return !stored.isAir() ? stored.getVisualShape(reader, pos, context) : super.getVisualShape(state, reader, pos, context);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        ItemStack itemstack = new ItemStack(this);
        NBTHelper.putTag(itemstack, "stored_state", NbtUtils.writeBlockState(Blocks.AIR.defaultBlockState()));
        return itemstack;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ColorizerBlockEntity(pos, state);
    }

    @Override
    public float getFriction(BlockState state, LevelReader levelReader, BlockPos pos, @Nullable Entity entity) {
        return this.getStoredState(levelReader, pos) != Blocks.AIR.defaultBlockState() ? this.getStoredState(levelReader, pos).getBlock().getFriction() : state.getBlock().getFriction();
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader levelReader, BlockPos pos, @Nullable Entity entity) {
        return this.getStoredState(levelReader, pos) != Blocks.AIR.defaultBlockState() ? this.getStoredState(levelReader, pos).getSoundType() : state.getSoundType();
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, SignalGetter signalGetter, BlockPos pos, Direction side) {
        return state.isRedstoneConductor(signalGetter, pos);
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter blockGetter, BlockPos pos, Player player) {
        return player.hasCorrectToolForDrops(state);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, BlockGetter blockGetter, BlockPos pos, Player player) {
        return blockGetter instanceof LevelReader levelReader ? this.getCloneItemStack(levelReader, pos, state, true) : new ItemStack(this);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter blockGetter, BlockPos position, Explosion explosion) {
        return super.getExplosionResistance();
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return ServerEffectUtils.addLandingEffects(this.getStoredState(level, pos), level, entity, numberOfParticles);
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        return ServerEffectUtils.addRunningEffects(this.getStoredState(level, pos), level, entity);
    }

    /** NeoForge gives every Block a default of the same name (IBlockExtension), so the choice has to be spelled out. */
    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return IColorizer.super.getLightEmission(state, world, pos);
    }
}

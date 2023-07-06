package com.grim3212.assorted.decor.common.blocks.colorizer;

import com.grim3212.assorted.decor.api.colorizer.IColorizer;
import com.grim3212.assorted.decor.common.blocks.blockentity.ColorizerBlockEntity;
import com.grim3212.assorted.lib.core.block.effects.ServerEffectUtils;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ColorizerTrapDoorBlock extends TrapDoorBlock implements IColorizer, EntityBlock {

    public ColorizerTrapDoorBlock() {
        super(Block.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5f, 12.0f).sound(SoundType.STONE).dynamicShape().noOcclusion(), BlockSetType.POLISHED_BLACKSTONE);
    }

    /// ===============================================
    /// ======== DEFAULT COLORIZER STUFF BELOW ========
    /// ===============================================
    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        BlockState stored = this.getStoredState(reader, pos);
        return !stored.isAir() ? stored.propagatesSkylightDown(reader, pos) : super.propagatesSkylightDown(state, reader, pos);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        BlockState stored = this.getStoredState(reader, pos);
        return !stored.isAir() ? stored.getVisualShape(reader, pos, context) : super.getVisualShape(state, reader, pos, context);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack itemstack = new ItemStack(this);
        NBTHelper.putTag(itemstack, "stored_state", NbtUtils.writeBlockState(Blocks.AIR.defaultBlockState()));
        return itemstack;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ColorizerBlockEntity(pos, state);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return this.getStoredState(world, pos) != Blocks.AIR.defaultBlockState() ? this.getStoredState(world, pos).getLightEmission() : state.getLightEmission();
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
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter blockGetter, BlockPos pos, Player player) {
        return super.getCloneItemStack(blockGetter, pos, state);
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
}

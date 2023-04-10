package com.grim3212.assorted.decor.common.blocks.colorizer;

import com.grim3212.assorted.decor.api.colorizer.IColorizer;
import com.grim3212.assorted.decor.common.blocks.blockentity.ColorizerBlockEntity;
import com.grim3212.assorted.lib.core.block.*;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class ColorizerSlabBlock extends SlabBlock implements IColorizer, EntityBlock, IBlockExtraProperties, IBlockSoundType, IBlockLightEmission, IBlockCanHarvest, IBlockCloneStack {

    public ColorizerSlabBlock() {
        super(Block.Properties.of(Material.STONE).strength(1.5f, 12.0f).sound(SoundType.STONE).dynamicShape().noOcclusion());
    }

    /// ===============================================
    /// ======== DEFAULT COLORIZER STUFF BELOW ========
    /// ===============================================
    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return this.getStoredState(reader, pos) != Blocks.AIR.defaultBlockState() ? this.getStoredState(reader, pos).propagatesSkylightDown(reader, pos) : super.propagatesSkylightDown(state, reader, pos);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return this.getStoredState(reader, pos) != Blocks.AIR.defaultBlockState() ? this.getStoredState(reader, pos).getVisualShape(reader, pos, context) : super.getVisualShape(state, reader, pos, context);
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
    public boolean shouldCheckWeakPower(BlockState state, LevelReader levelReader, BlockPos pos, Direction side) {
        return state.isRedstoneConductor(levelReader, pos);
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
}
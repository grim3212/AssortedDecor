package com.grim3212.assorted.decor.common.block;

import com.grim3212.assorted.decor.common.block.tileentity.ColorizerTileEntity;
import com.grim3212.assorted.decor.common.handler.DecorConfig;
import com.grim3212.assorted.decor.common.util.NBTHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ColorizerFenceGateBlock extends FenceGateBlock implements IColorizer {

	public ColorizerFenceGateBlock() {
		super(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5f, 12.0f).sound(SoundType.STONE).variableOpacity().notSolid());
	}

	/// ===============================================
	/// ======== DEFAULT COLORIZER STUFF BELOW ========
	/// ===============================================
	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return this.getStoredState(reader, pos) != Blocks.AIR.getDefaultState() ? this.getStoredState(reader, pos).propagatesSkylightDown(reader, pos) : super.propagatesSkylightDown(state, reader, pos);
	}

	@Override
	public VoxelShape getRayTraceShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
		return this.getStoredState(reader, pos) != Blocks.AIR.getDefaultState() ? this.getStoredState(reader, pos).getRaytraceShape(reader, pos, context) : super.getRayTraceShape(state, reader, pos, context);
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return this.getStoredState(world, pos) != Blocks.AIR.getDefaultState() ? this.getStoredState(world, pos).getLightValue(world, pos) : super.getLightValue(state, world, pos);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBlockHarvested(worldIn, pos, state, player);
		if (DecorConfig.COMMON.consumeBlock.get()) {
			if (!player.abilities.isCreativeMode) {
				if (this.getStoredState(worldIn, pos) != Blocks.AIR.getDefaultState()) {
					BlockState blockState = this.getStoredState(worldIn, pos);
					spawnAsEntity(worldIn, pos, new ItemStack(blockState.getBlock(), 1));
				}
			}
		}
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		ItemStack itemstack = new ItemStack(this);
		NBTHelper.putTag(itemstack, "stored_state", NBTUtil.writeBlockState(Blocks.AIR.getDefaultState()));
		return itemstack;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ColorizerTileEntity();
	}

	@Override
	public boolean addLandingEffects(BlockState state, ServerWorld worldObj, BlockPos blockPosition, BlockState iblockstate, LivingEntity entity, int numberOfParticles) {
		TileEntity tileentity = (TileEntity) worldObj.getTileEntity(blockPosition);
		if (tileentity instanceof ColorizerTileEntity) {
			ColorizerTileEntity te = (ColorizerTileEntity) tileentity;
			if (te.getStoredBlockState() == Blocks.AIR.getDefaultState()) {
				return super.addLandingEffects(state, worldObj, blockPosition, iblockstate, entity, numberOfParticles);
			} else {
				worldObj.spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, te.getStoredBlockState()), entity.getPosX(), entity.getPosY(), entity.getPosZ(), numberOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
			}
		}
		return true;
	}

}

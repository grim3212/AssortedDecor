package com.grim3212.assorted.decor.common.block.colorizer;

import com.grim3212.assorted.decor.common.block.IColorizer;
import com.grim3212.assorted.decor.common.block.blockentity.ColorizerBlockEntity;
import com.grim3212.assorted.decor.common.util.NBTHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ColorizerDoorBlock extends DoorBlock implements IColorizer, EntityBlock {

	public ColorizerDoorBlock() {
		super(Block.Properties.of(Material.STONE).strength(1.5f, 12.0f).sound(SoundType.STONE).dynamicShape().noOcclusion());
	}

	@Override
	public boolean clearColorizer(Level worldIn, BlockPos pos, Player player, InteractionHand hand) {
		if (IColorizer.super.clearColorizer(worldIn, pos, player, hand)) {
			BlockState state = worldIn.getBlockState(pos);

			if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
				return IColorizer.super.clearColorizer(worldIn, pos.above(), player, hand);
			}
			return IColorizer.super.clearColorizer(worldIn, pos.below(), player, hand);
		}

		return false;
	}

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
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		return this.getStoredState(world, pos) != Blocks.AIR.defaultBlockState() ? this.getStoredState(world, pos).getLightEmission(world, pos) : super.getLightEmission(state, world, pos);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
		ItemStack itemstack = new ItemStack(this);
		NBTHelper.putTag(itemstack, "stored_state", NbtUtils.writeBlockState(Blocks.AIR.defaultBlockState()));
		return itemstack;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ColorizerBlockEntity(pos, state);
	}

	@Override
	public boolean addLandingEffects(BlockState state, ServerLevel worldObj, BlockPos blockPosition, BlockState iblockstate, LivingEntity entity, int numberOfParticles) {
		BlockEntity tileentity = (BlockEntity) worldObj.getBlockEntity(blockPosition);
		if (tileentity instanceof ColorizerBlockEntity) {
			ColorizerBlockEntity te = (ColorizerBlockEntity) tileentity;
			if (te.getStoredBlockState() == Blocks.AIR.defaultBlockState()) {
				return super.addLandingEffects(state, worldObj, blockPosition, iblockstate, entity, numberOfParticles);
			} else {
				worldObj.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, te.getStoredBlockState()), entity.getX(), entity.getY(), entity.getZ(), numberOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
			}
		}
		return true;
	}
}

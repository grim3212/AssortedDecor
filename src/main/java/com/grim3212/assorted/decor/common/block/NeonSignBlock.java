package com.grim3212.assorted.decor.common.block;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.tileentity.NeonSignTileEntity;
import com.grim3212.assorted.decor.common.item.DecorItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NeonSignBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {

	protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

	protected NeonSignBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.getValue(SignBlock.WATERLOGGED)) {
			worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}

		return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public boolean isPossibleToRespawnInThis() {
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new NeonSignTileEntity(pos, state);
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state) {
		return new ItemStack(DecorItems.NEON_SIGN.get());
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (worldIn.isClientSide) {
			AssortedDecor.proxy.openNeonSign((NeonSignTileEntity) worldIn.getBlockEntity(pos));
			return InteractionResult.SUCCESS;
		} else {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);
			if (tileentity instanceof NeonSignTileEntity) {
				NeonSignTileEntity sign = (NeonSignTileEntity) tileentity;
				return sign.executeCommand(player) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
			}
			return InteractionResult.FAIL;
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(SignBlock.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
}

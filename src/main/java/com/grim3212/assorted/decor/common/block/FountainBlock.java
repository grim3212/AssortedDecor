package com.grim3212.assorted.decor.common.block;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class FountainBlock extends Block {

	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public FountainBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(ACTIVE, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean flg) {
		if (!level.isClientSide) {
			boolean flag = state.getValue(ACTIVE);
			if (flag != level.hasNeighborSignal(pos)) {
				if (flag) {
					level.scheduleTick(pos, this, 4);
				} else {
					level.setBlock(pos, state.cycle(ACTIVE), 2);
				}
			}

		}
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random rand) {
		if (state.getValue(ACTIVE) && !level.hasNeighborSignal(pos)) {
			level.setBlock(pos, state.cycle(ACTIVE), 2);
		}

	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ACTIVE);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, Random rand) {
		if (state.getValue(ACTIVE)) {
			level.addParticle(ParticleTypes.SPLASH, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0D, 1.0D, 0.0D);
			level.addParticle(ParticleTypes.SPLASH, pos.getX() + 0.5D, pos.getY() + 1.25D, pos.getZ() + 0.5D, 0.0D, 1.0D, 0.0D);
			level.addParticle(ParticleTypes.SPLASH, pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D, 0.0D, 1.0D, 0.0D);
			level.addParticle(ParticleTypes.SPLASH, pos.getX() + 0.5D, pos.getY() + 1.75D, pos.getZ() + 0.5D, 0.0D, 1.0D, 0.0D);
			level.addParticle(ParticleTypes.SPLASH, pos.getX() + 0.5D, pos.getY() + 2.0D, pos.getZ() + 0.5D, 0.0D, 1.0D, 0.0D);
			level.addParticle(ParticleTypes.SPLASH, pos.getX() + 0.5D, pos.getY() + 2.25D, pos.getZ() + 0.5D, 0.0D, 1.0D, 0.0D);
			level.addParticle(ParticleTypes.SPLASH, pos.getX() + 0.5D, pos.getY() + 2.5D, pos.getZ() + 0.5D, 0.0D, 1.0D, 0.0D);
			level.addParticle(ParticleTypes.SPLASH, pos.getX() + 0.5D, pos.getY() + 2.75D, pos.getZ() + 0.5D, 0.0D, 1.0D, 0.0D);
			level.addParticle(ParticleTypes.SPLASH, pos.getX() + 0.5D, pos.getY() + 3.0D, pos.getZ() + 0.5D, 0.0D, 1.0D, 0.0D);
			level.addParticle(ParticleTypes.SPLASH, pos.getX() + 0.5D, pos.getY() + 3.25D, pos.getZ() + 0.5D, 0.0D, 1.0D, 0.0D);
			level.addParticle(ParticleTypes.SPLASH, pos.getX() + 0.5D, pos.getY() + 3.5D, pos.getZ() + 0.5D, 0.0D, 1.0D, 0.0D);
		}
	}
}

package com.grim3212.assorted.decor.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RoadwayManholeBlock extends Block {

	public static final BooleanProperty OPEN = BooleanProperty.create("open");
	protected static final VoxelShape RENDER_SHAPE = box(0.01D, 0.01D, 0.01D, 15.99D, 15.99D, 15.99D);
	protected static final VoxelShape OPEN_SHAPE = Shapes.join(box(0.01D, 0.01D, 0.01D, 15.99D, 15.99D, 15.99D), box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D), BooleanOp.ONLY_FIRST);

	public RoadwayManholeBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(OPEN, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(OPEN);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!player.getAbilities().mayBuild) {
			return InteractionResult.PASS;
		} else {
			boolean isOpen = state.getValue(OPEN);

			level.playSound(null, pos, isOpen ? SoundEvents.IRON_TRAPDOOR_CLOSE : SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 0.3F, 0.6F);
			level.setBlock(pos, state.cycle(OPEN), 3);
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos pos) {
		return state.getValue(OPEN) ? true : false;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return state.getValue(OPEN) ? RENDER_SHAPE : Shapes.block();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return state.getValue(OPEN) ? OPEN_SHAPE : Shapes.block();
	}
}

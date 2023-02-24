package com.grim3212.assorted.decor.common.blocks.colorizer;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

public class ColorizerFourWayBlock extends ColorizerBlock implements SimpleWaterloggedBlock {

	public static final BooleanProperty NORTH = PipeBlock.NORTH;
	public static final BooleanProperty EAST = PipeBlock.EAST;
	public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
	public static final BooleanProperty WEST = PipeBlock.WEST;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((facingProperty) -> {
		return facingProperty.getKey().getAxis().isHorizontal();
	}).collect(Util.toMap());
	protected final VoxelShape[] collisionShapes;
	protected final VoxelShape[] shapes;
	private final Object2IntMap<BlockState> stateMap = new Object2IntOpenHashMap<>();

	public ColorizerFourWayBlock(float nodeWidth, float extensionWidth, float nodeHeight, float extensionHeight, float collisionY) {
		this.collisionShapes = this.makeShapes(nodeWidth, extensionWidth, collisionY, 0.0F, collisionY);
		this.shapes = this.makeShapes(nodeWidth, extensionWidth, nodeHeight, 0.0F, extensionHeight);

		for (BlockState blockstate : this.stateDefinition.getPossibleStates()) {
			this.getIndex(blockstate);
		}
	}

	protected VoxelShape[] makeShapes(float nodeWidth, float extensionWidth, float nodeHeight, float extensionBottom, float extensionHeight) {
		float f = 8.0F - nodeWidth;
		float f1 = 8.0F + nodeWidth;
		float f2 = 8.0F - extensionWidth;
		float f3 = 8.0F + extensionWidth;
		VoxelShape voxelshape = Block.box((double) f, 0.0D, (double) f, (double) f1, (double) nodeHeight, (double) f1);
		VoxelShape voxelshape1 = Block.box((double) f2, (double) extensionBottom, 0.0D, (double) f3, (double) extensionHeight, (double) f3);
		VoxelShape voxelshape2 = Block.box((double) f2, (double) extensionBottom, (double) f2, (double) f3, (double) extensionHeight, 16.0D);
		VoxelShape voxelshape3 = Block.box(0.0D, (double) extensionBottom, (double) f2, (double) f3, (double) extensionHeight, (double) f3);
		VoxelShape voxelshape4 = Block.box((double) f2, (double) extensionBottom, (double) f2, 16.0D, (double) extensionHeight, (double) f3);
		VoxelShape voxelshape5 = Shapes.or(voxelshape1, voxelshape4);
		VoxelShape voxelshape6 = Shapes.or(voxelshape2, voxelshape3);
		VoxelShape[] avoxelshape = new VoxelShape[] { Shapes.empty(), voxelshape2, voxelshape3, voxelshape6, voxelshape1, Shapes.or(voxelshape2, voxelshape1), Shapes.or(voxelshape3, voxelshape1), Shapes.or(voxelshape6, voxelshape1), voxelshape4, Shapes.or(voxelshape2, voxelshape4), Shapes.or(voxelshape3, voxelshape4), Shapes.or(voxelshape6, voxelshape4), voxelshape5, Shapes.or(voxelshape2, voxelshape5), Shapes.or(voxelshape3, voxelshape5),
				Shapes.or(voxelshape6, voxelshape5) };

		for (int i = 0; i < 16; ++i) {
			avoxelshape[i] = Shapes.or(voxelshape, avoxelshape[i]);
		}

		return avoxelshape;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return !state.getValue(WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return this.shapes[this.getIndex(state)];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return this.collisionShapes[this.getIndex(state)];
	}

	private static int getMask(Direction facing) {
		return 1 << facing.get2DDataValue();
	}

	protected int getIndex(BlockState state) {
		return this.stateMap.computeIntIfAbsent(state, (mapState) -> {
			int i = 0;
			if (mapState.getValue(NORTH)) {
				i |= getMask(Direction.NORTH);
			}

			if (mapState.getValue(EAST)) {
				i |= getMask(Direction.EAST);
			}

			if (mapState.getValue(SOUTH)) {
				i |= getMask(Direction.SOUTH);
			}

			if (mapState.getValue(WEST)) {
				i |= getMask(Direction.WEST);
			}

			return i;
		});
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		switch (rot) {
		case CLOCKWISE_180:
			return state.setValue(NORTH, state.getValue(SOUTH)).setValue(EAST, state.getValue(WEST)).setValue(SOUTH, state.getValue(NORTH)).setValue(WEST, state.getValue(EAST));
		case COUNTERCLOCKWISE_90:
			return state.setValue(NORTH, state.getValue(EAST)).setValue(EAST, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(WEST)).setValue(WEST, state.getValue(NORTH));
		case CLOCKWISE_90:
			return state.setValue(NORTH, state.getValue(WEST)).setValue(EAST, state.getValue(NORTH)).setValue(SOUTH, state.getValue(EAST)).setValue(WEST, state.getValue(SOUTH));
		default:
			return state;
		}
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		switch (mirrorIn) {
		case LEFT_RIGHT:
			return state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
		case FRONT_BACK:
			return state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
		default:
			return super.mirror(state, mirrorIn);
		}
	}

}

package com.grim3212.assorted.decor.common.block.colorizer;

import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;
import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.DecorBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ColorizerFireplaceBlock extends ColorizerFireplaceBaseBlock {
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = Util.make(Maps.newEnumMap(Direction.class), (map) -> {
		map.put(Direction.NORTH, NORTH);
		map.put(Direction.EAST, EAST);
		map.put(Direction.SOUTH, SOUTH);
		map.put(Direction.WEST, WEST);
	});

	public ColorizerFireplaceBlock() {
		this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false).setValue(EAST, false).setValue(WEST, false).setValue(SOUTH, false).setValue(NORTH, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(ACTIVE, EAST, WEST, NORTH, SOUTH);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (worldIn.getBlockState(pos).getValue(ACTIVE) && worldIn.getBlockState(pos.above()).getBlock() == DecorBlocks.COLORIZER_CHIMNEY.get()) {
			int smokeheight = 1;
			while (worldIn.getBlockState(pos.above(smokeheight)).getBlock() == DecorBlocks.COLORIZER_CHIMNEY.get()) {
				smokeheight++;
			}

			AssortedDecor.proxy.produceSmoke(worldIn, pos.above(smokeheight), 0.5D, 0.0D, 0.5D, 1, true);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockPos blockpos = context.getClickedPos();
		BlockPos northPos = blockpos.north();
		BlockPos eastPos = blockpos.east();
		BlockPos southPos = blockpos.south();
		BlockPos westPos = blockpos.west();
		return super.getStateForPlacement(context).setValue(NORTH, this.canConnectTo(context.getLevel(), northPos)).setValue(EAST, this.canConnectTo(context.getLevel(), eastPos)).setValue(SOUTH, this.canConnectTo(context.getLevel(), southPos)).setValue(WEST, this.canConnectTo(context.getLevel(), westPos));
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		return facing.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? stateIn.setValue(FACING_TO_PROPERTY_MAP.get(facing), this.canConnectTo(worldIn, facingPos)) : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	public boolean canConnectTo(IBlockReader worldIn, BlockPos pos) {
		BlockState blockState = worldIn.getBlockState(pos).getBlock().defaultBlockState();
		return blockState == this.defaultBlockState();
	}
}

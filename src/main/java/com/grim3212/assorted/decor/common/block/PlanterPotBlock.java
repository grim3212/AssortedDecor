package com.grim3212.assorted.decor.common.block;

import java.util.Random;

import com.grim3212.assorted.decor.common.block.colorizer.ColorizerStoolBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class PlanterPotBlock extends Block {

	public static final IntegerProperty TOP = IntegerProperty.create("top", 0, 6);
	public static final BooleanProperty DOWN = BooleanProperty.create("down");

	public PlanterPotBlock() {
		super(Properties.create(Material.CLAY).sound(SoundType.GROUND).tickRandomly().hardnessAndResistance(0.5f, 10f).variableOpacity().notSolid());
		this.setDefaultState(this.stateContainer.getBaseState().with(TOP, 0).with(DOWN, false));
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(TOP, DOWN);
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {
		BlockState plant = plantable.getPlant(world, pos.offset(facing));
		PlantType plantType = plantable.getPlantType(world, pos.offset(facing));

		if (plantType == PlantType.CAVE) {
			return true;
		}

		if (world.getBlockState(pos).getBlock() == DecorBlocks.PLANTER_POT.get()) {
			int top = world.getBlockState(pos).get(TOP);
			switch (top) {
			case 0:
				if ((plantType == PlantType.PLAINS && !this.isStool(world, pos)) || (plantType == PlantType.PLAINS && plant.getBlock() instanceof FlowerBlock)) {
					return true;
				}

				if (plantType == PlantType.BEACH && !this.isStool(world, pos)) {
					boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER || world.getBlockState(pos.west()).getMaterial() == Material.WATER || world.getBlockState(pos.north()).getMaterial() == Material.WATER || world.getBlockState(pos.south()).getMaterial() == Material.WATER);
					return hasWater;
				}
				break;
			case 1:
				if ((plantType == PlantType.DESERT && !this.isStool(world, pos)) || (plantType == PlantType.DESERT && plant.getBlock() == Blocks.DEAD_BUSH)) {
					return true;
				}
				if (plantType == PlantType.BEACH && !this.isStool(world, pos)) {
					boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER || world.getBlockState(pos.west()).getMaterial() == Material.WATER || world.getBlockState(pos.north()).getMaterial() == Material.WATER || world.getBlockState(pos.south()).getMaterial() == Material.WATER);
					return hasWater;
				}
				break;
			case 2:
				return false;
			case 3:
				return false;
			case 4:
				if (plantType == PlantType.CROP && !this.isStool(world, pos))
					return true;
				break;
			case 5:
				return false;
			case 6:
				if (plantType == PlantType.NETHER && !this.isStool(world, pos))
					return true;
				break;
			}
		}
		return false;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		worldIn.notifyNeighborsOfStateChange(pos, this);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
		worldIn.getPendingBlockTicks().scheduleTick(pos, this, 10);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext ctx) {
		if (isStool(worldIn, pos))
			return ColorizerStoolBlock.POT_STOOL;
		return VoxelShapes.fullCube();
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (worldIn.isRemote)
			return ActionResultType.SUCCESS;

		if (player.getHeldItem(hand).isEmpty() || player.getHeldItem(hand).getCount() == 0) {
			int top = worldIn.getBlockState(pos).get(TOP);
			if (top == 6) {
				top = 0;
			} else {
				top++;
			}
			worldIn.setBlockState(pos, state.with(TOP, top), 2);
			return ActionResultType.SUCCESS;
		} else {
			return ActionResultType.PASS;
		}
	}

	private boolean isStool(IBlockReader worldIn, BlockPos pos) {
		BlockState stoolState = worldIn.getBlockState(pos.down());
		if (stoolState.getBlock() == DecorBlocks.COLORIZER_STOOL.get()) {
			return stoolState.get(BlockStateProperties.FACE) == AttachFace.FLOOR;
		}

		return false;
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		return stateIn.with(DOWN, this.isStool(worldIn, currentPos));
	}
}

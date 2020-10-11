package com.grim3212.assorted.decor.common.util;

import com.grim3212.assorted.decor.common.block.ColorizerSlopeBlock;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.handler.DecorConfig;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.Half;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class DecorUtil {

	public static String[] getDyeNames() {
		DyeColor[] states = DyeColor.values();
		String[] names = new String[states.length];

		for (int i = 0; i < states.length; i++) {
			names[i] = states[i].name();
		}

		return names;
	}

	public static ItemStack createFurnitureWithState(Block block, BlockState toPlace) {
		ItemStack furniture = new ItemStack(block);
		NBTHelper.putTag(furniture, "stored_state", NBTUtil.writeBlockState(toPlace));
		return furniture;
	}

	/**
	 * Adds the AxisAlginedBB that it generates using getCollision to the blocks
	 * colliding boxes list
	 * 
	 * @param pos            The pos we are checking for collision at
	 * @param entityBox      The entitybox to make sure it is intersecting
	 * @param collidingBoxes The blocks colliding blocks list
	 * @param state          The blockstate we are going to be evaluating
	 * @param numPieces      The number of pieces that this Slope has
	 */
	public static VoxelShape addAxisAlignedBoxes(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context, int numPieces) {
		VoxelShape endShape = VoxelShapes.empty();

		for (int piece = 0; piece < DecorConfig.COMMON.smoothness.get(); piece++) {
			for (int smoothness = 0; smoothness < DecorConfig.COMMON.smoothness.get(); smoothness++) {
				VoxelShape collision = getCollision(state, piece, smoothness, numPieces);

				if (collision != VoxelShapes.empty()) {
					endShape = VoxelShapes.or(endShape, collision);
				}
			}
		}

		return endShape;
	}

	/**
	 * Returns a new AxisAlignedBB for the specified piece at the smoothness level
	 * 
	 * Based off of CarpentryBlocks
	 * https://github.com/Mineshopper/carpentersblocks/blob/master/src/main/java
	 * /com/carpentersblocks/util/slope/SlopeUtil.java#L56
	 * 
	 * @param state      The BlockState that we are grabbing the collision for
	 * @param piece      The current piece that we are on
	 * @param smoothness The current smoothness we are evaluating
	 * @param numPieces  The number of pieces that this Slope has
	 * 
	 * @return A new AxisAlignedBB at this smoothness level
	 */
	public static VoxelShape getCollision(BlockState state, int piece, int smoothness, int numPieces) {
		piece++;

		float zeroPieceOffset = (float) (piece - 1) / numPieces;
		float onePieceOffset = (float) piece / numPieces;

		float zeroOffset = (float) smoothness / DecorConfig.COMMON.smoothness.get();
		float oneOffset = (float) (smoothness + 1) / DecorConfig.COMMON.smoothness.get();

		if (state.getBlock() instanceof ColorizerSlopeBlock) {
			if (state.getBlock() == DecorBlocks.COLORIZER_SLOPE.get()) {
				if (state.get(ColorizerSlopeBlock.HALF) == Half.BOTTOM) {
					switch (state.get(ColorizerSlopeBlock.FACING)) {
					case EAST:
						return VoxelShapes.create(new AxisAlignedBB(zeroOffset, 0.0F, 0.0F, 1.0F, oneOffset, 1.0F));
					case NORTH:
						return VoxelShapes.create(new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F - zeroOffset, oneOffset));
					case SOUTH:
						return VoxelShapes.create(new AxisAlignedBB(0.0F, 0.0F, zeroOffset, 1.0F, oneOffset, 1.0F));
					case WEST:
						return VoxelShapes.create(new AxisAlignedBB(0.0F, 0.0F, 0.0F, oneOffset, 1.0F - zeroOffset, 1.0F));
					default:
						return VoxelShapes.empty();
					}
				} else {
					switch (state.get(ColorizerSlopeBlock.FACING)) {
					case EAST:
						return VoxelShapes.create(new AxisAlignedBB(zeroOffset, 1.0F - oneOffset, 0.0F, 1.0F, 1.0F, 1.0F));
					case NORTH:
						return VoxelShapes.create(new AxisAlignedBB(0.0F, zeroOffset, 0.0F, 1.0F, 1.0F, oneOffset));
					case SOUTH:
						return VoxelShapes.create(new AxisAlignedBB(0.0F, 1.0F - oneOffset, zeroOffset, 1.0F, 1.0F, 1.0F));
					case WEST:
						return VoxelShapes.create(new AxisAlignedBB(0.0F, zeroOffset, 0.0F, oneOffset, 1.0F, 1.0F));
					default:
						return VoxelShapes.empty();
					}
				}
			}

		}

		return VoxelShapes.empty();
	}

	public enum SlopeType {
		SLANTED_CORNER("slanted_corner"),
		CORNER("corner", 2),
		SLOPE("slope", 2),
		SLOPED_ANGLE("sloped_angle", 2),
		OBLIQUE_SLOPE("oblique_slope", 3),
		SLOPED_INTERSECTION("sloped_intersection", 2),
		PYRAMID("pyramid", 2),
		FULL_PYRAMID("full_pyramid", 2),
		SLOPED_POST("sloped_post", 1);

		private final int numPieces;
		private final String name;

		private SlopeType(String name) {
			this(name, -1);
		}

		private SlopeType(String name, int numPieces) {
			this.name = name;
			this.numPieces = numPieces;
		}

		public String getName() {
			return name;
		}

		public int getNumPieces() {
			// Special override for SlantedAngles
			if (this == SLANTED_CORNER)
				return DecorConfig.COMMON.smoothness.get();
			else
				return numPieces;
		}
	}
}

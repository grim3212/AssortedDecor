package com.grim3212.assorted.decor.common.util;

import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerSlopeBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerSlopeSideBlock;
import com.grim3212.assorted.decor.common.handler.DecorConfig;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

public class ColorizerUtil {

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
		NBTHelper.putTag(furniture, "stored_state", NbtUtils.writeBlockState(toPlace));
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
	public static VoxelShape addAxisAlignedBoxes(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context, int numPieces) {
		VoxelShape endShape = Shapes.empty();

		for (int piece = 0; piece < DecorConfig.COMMON.smoothness.get(); piece++) {
			for (int smoothness = 0; smoothness < DecorConfig.COMMON.smoothness.get(); smoothness++) {
				VoxelShape collision = getCollision(state, piece, smoothness, numPieces);

				if (collision != Shapes.empty()) {
					endShape = Shapes.or(endShape, collision);
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
				if (state.getValue(ColorizerSlopeBlock.HALF) == Half.BOTTOM) {
					switch (state.getValue(ColorizerSlopeBlock.FACING)) {
					case EAST:
						return Shapes.create(new AABB(zeroOffset, 0.0F, 0.0F, 1.0F, oneOffset, 1.0F));
					case NORTH:
						return Shapes.create(new AABB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F - zeroOffset, oneOffset));
					case SOUTH:
						return Shapes.create(new AABB(0.0F, 0.0F, zeroOffset, 1.0F, oneOffset, 1.0F));
					case WEST:
						return Shapes.create(new AABB(0.0F, 0.0F, 0.0F, oneOffset, 1.0F - zeroOffset, 1.0F));
					default:
						return Shapes.empty();
					}
				} else {
					switch (state.getValue(ColorizerSlopeBlock.FACING)) {
					case EAST:
						return Shapes.create(new AABB(zeroOffset, 1.0F - oneOffset, 0.0F, 1.0F, 1.0F, 1.0F));
					case NORTH:
						return Shapes.create(new AABB(0.0F, zeroOffset, 0.0F, 1.0F, 1.0F, oneOffset));
					case SOUTH:
						return Shapes.create(new AABB(0.0F, 1.0F - oneOffset, zeroOffset, 1.0F, 1.0F, 1.0F));
					case WEST:
						return Shapes.create(new AABB(0.0F, zeroOffset, 0.0F, oneOffset, 1.0F, 1.0F));
					default:
						return Shapes.empty();
					}
				}
			} else if (state.getBlock() == DecorBlocks.COLORIZER_SLOPED_ANGLE.get()) {
				if (state.getValue(ColorizerSlopeBlock.HALF) == Half.BOTTOM) {
					switch (state.getValue(ColorizerSlopeBlock.FACING)) {
					case NORTH:
						return Shapes.create(new AABB(zeroOffset, 0.0F, 0.0F, 1.0F, oneOffset, 1.0F - zeroOffset));
					case WEST:
						return Shapes.create(new AABB(0.0F, 0.0F, 0.0F, 1.0F - zeroOffset, oneOffset, 1.0F - zeroOffset));
					case EAST:
						return Shapes.create(new AABB(zeroOffset, 0.0F, zeroOffset, 1.0F, oneOffset, 1.0F));
					case SOUTH:
						return Shapes.create(new AABB(0.0F, 0.0F, zeroOffset, 1.0F - zeroOffset, oneOffset, 1.0F));
					default:
						return Shapes.empty();
					}
				} else {
					switch (state.getValue(ColorizerSlopeBlock.FACING)) {
					case EAST:
						return Shapes.create(new AABB(zeroOffset, 1.0F - oneOffset, 0.0F, 1.0F, 1.0F, 1.0F - zeroOffset));
					case NORTH:
						return Shapes.create(new AABB(0.0F, 1.0F - oneOffset, 0.0F, 1.0F - zeroOffset, 1.0F, 1.0F - zeroOffset));
					case SOUTH:
						return Shapes.create(new AABB(zeroOffset, 1.0F - oneOffset, zeroOffset, 1.0F, 1.0F, 1.0F));
					case WEST:
						return Shapes.create(new AABB(0.0F, 1.0F - oneOffset, zeroOffset, 1.0F - zeroOffset, 1.0F, 1.0F));
					default:
						return Shapes.empty();
					}
				}
			} else if (state.getBlock() == DecorBlocks.COLORIZER_SLANTED_CORNER.get()) {
				if (state.getValue(ColorizerSlopeBlock.HALF) == Half.BOTTOM) {
					switch (state.getValue(ColorizerSlopeBlock.FACING)) {
					case EAST:
						return Shapes.create(new AABB(zeroPieceOffset + zeroOffset * (1.0F - zeroPieceOffset), 0.0F, 0.0F, 1.0F, onePieceOffset, oneOffset * (1.0F - zeroPieceOffset)));
					case NORTH:
						return Shapes.create(new AABB(0.0F, 0.0F, 0.0F, oneOffset * (1.0F - zeroPieceOffset), onePieceOffset, 1.0F - zeroPieceOffset - zeroOffset * (1.0F - zeroPieceOffset)));
					case SOUTH:
						return Shapes.create(new AABB(zeroPieceOffset + zeroOffset * (1.0F - zeroPieceOffset), 0.0F, 1.0F - oneOffset * (1.0F - zeroPieceOffset), 1.0F, onePieceOffset, 1.0F));
					case WEST:
						return Shapes.create(new AABB(0.0F, 0.0F, zeroPieceOffset + zeroOffset * (1.0F - zeroPieceOffset), oneOffset * (1.0F - zeroPieceOffset), onePieceOffset, 1.0F));
					default:
						return Shapes.empty();
					}
				} else {
					switch (state.getValue(ColorizerSlopeBlock.FACING)) {
					case NORTH:
						return Shapes.create(new AABB(zeroPieceOffset + zeroOffset * (1.0F - zeroPieceOffset), 1.0F - onePieceOffset, 0.0F, 1.0F, 1.0F, oneOffset * (1.0F - zeroPieceOffset)));
					case WEST:
						return Shapes.create(new AABB(0.0F, 1.0F - onePieceOffset, 0.0F, oneOffset * (1.0F - zeroPieceOffset), 1.0F, 1.0F - zeroPieceOffset - zeroOffset * (1.0F - zeroPieceOffset)));
					case EAST:
						return Shapes.create(new AABB(zeroPieceOffset + zeroOffset * (1.0F - zeroPieceOffset), 1.0F - onePieceOffset, 1.0F - oneOffset * (1.0F - zeroPieceOffset), 1.0F, 1.0F, 1.0F));
					case SOUTH:
						return Shapes.create(new AABB(0.0F, 1.0F - onePieceOffset, zeroPieceOffset + zeroOffset * (1.0F - zeroPieceOffset), oneOffset * (1.0F - zeroPieceOffset), 1.0F, 1.0F));
					default:
						return Shapes.empty();
					}
				}
			} else if (state.getBlock() == DecorBlocks.COLORIZER_OBLIQUE_SLOPE.get()) {
				if (state.getValue(ColorizerSlopeBlock.HALF) == Half.BOTTOM) {
					switch (state.getValue(ColorizerSlopeBlock.FACING)) {
					case EAST:
						switch (piece) {
						case 1:
							return Shapes.create(new AABB(zeroOffset, 0.0F, 0.0F, 1.0F, 1.0F, oneOffset));
						case 2:
							return Shapes.create(new AABB(zeroOffset, 0.0F, 0.0F, 1.0F, oneOffset, 1.0F));
						case 3:
							return Shapes.create(new AABB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F - zeroOffset, oneOffset));
						}
					case NORTH:
						switch (piece) {
						case 1:
							return Shapes.create(new AABB(0.0F, 0.0F, 0.0F, oneOffset, 1.0F, 1.0F - zeroOffset));
						case 2:
							return Shapes.create(new AABB(0.0F, 0.0F, 0.0F, oneOffset, 1.0F - zeroOffset, 1.0F));
						case 3:
							return Shapes.create(new AABB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F - zeroOffset, oneOffset));
						}
					case SOUTH:
						switch (piece) {
						case 1:
							return Shapes.create(new AABB(zeroOffset, 0.0F, 1.0F - oneOffset, 1.0F, 1.0F, 1.0F));
						case 2:
							return Shapes.create(new AABB(zeroOffset, 0.0F, 0.0F, 1.0F, oneOffset, 1.0F));
						case 3:
							return Shapes.create(new AABB(0.0F, 0.0F, zeroOffset, 1.0F, oneOffset, 1.0F));
						}
					case WEST:
						switch (piece) {
						case 1:
							return Shapes.create(new AABB(0.0F, 0.0F, zeroOffset, oneOffset, 1.0F, 1.0F));
						case 2:
							return Shapes.create(new AABB(0.0F, 0.0F, 0.0F, oneOffset, 1.0F - zeroOffset, 1.0F));
						case 3:
							return Shapes.create(new AABB(0.0F, 0.0F, zeroOffset, 1.0F, oneOffset, 1.0F));
						}
					default:
						return Shapes.empty();
					}
				} else {
					switch (state.getValue(ColorizerSlopeBlock.FACING)) {
					case NORTH:
						switch (piece) {
						case 1:
							return Shapes.create(new AABB(zeroOffset, 0.0F, 0.0F, 1.0F, 1.0F, oneOffset));
						case 2:
							return Shapes.create(new AABB(zeroOffset, 1.0F - oneOffset, 0.0F, 1.0F, 1.0F, 1.0F));
						case 3:
							return Shapes.create(new AABB(0.0F, zeroOffset, 0.0F, 1.0F, 1.0F, oneOffset));
						}
					case WEST:
						switch (piece) {
						case 1:
							return Shapes.create(new AABB(0.0F, 0.0F, 0.0F, oneOffset, 1.0F, 1.0F - zeroOffset));
						case 2:
							return Shapes.create(new AABB(0.0F, zeroOffset, 0.0F, oneOffset, 1.0F, 1.0F));
						case 3:
							return Shapes.create(new AABB(0.0F, zeroOffset, 0.0F, 1.0F, 1.0F, oneOffset));
						}
						return Shapes.create(new AABB(0.0F, zeroOffset, 0.0F, 1.0F, 1.0F, oneOffset));
					case EAST:
						switch (piece) {
						case 1:
							return Shapes.create(new AABB(zeroOffset, 0.0F, 1.0F - oneOffset, 1.0F, 1.0F, 1.0F));
						case 2:
							return Shapes.create(new AABB(zeroOffset, 1.0F - oneOffset, 0.0F, 1.0F, 1.0F, 1.0F));
						case 3:
							return Shapes.create(new AABB(0.0F, 1.0F - oneOffset, zeroOffset, 1.0F, 1.0F, 1.0F));
						}
					case SOUTH:
						switch (piece) {
						case 1:
							return Shapes.create(new AABB(0.0F, 0.0F, zeroOffset, oneOffset, 1.0F, 1.0F));
						case 2:
							return Shapes.create(new AABB(0.0F, zeroOffset, 0.0F, oneOffset, 1.0F, 1.0F));
						case 3:
							return Shapes.create(new AABB(0.0F, 1.0F - oneOffset, zeroOffset, 1.0F, 1.0F, 1.0F));
						}
					default:
						return Shapes.empty();
					}
				}
			} else if (state.getBlock() == DecorBlocks.COLORIZER_SLOPED_INTERSECTION.get()) {
				if (state.getValue(ColorizerSlopeBlock.HALF) == Half.BOTTOM) {
					switch (state.getValue(ColorizerSlopeBlock.FACING)) {
					case EAST:
						switch (piece) {
						case 1:
							return Shapes.create(new AABB(zeroOffset, 0.0F, 0.0F, 1.0F, oneOffset, 1.0F));
						case 2:
							return Shapes.create(new AABB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F - zeroOffset, oneOffset));
						}
					case NORTH:
						switch (piece) {
						case 1:
							return Shapes.create(new AABB(0.0F, 0.0F, 0.0F, oneOffset, 1.0F - zeroOffset, 1.0F));
						case 2:
							return Shapes.create(new AABB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F - zeroOffset, oneOffset));
						}
					case SOUTH:
						switch (piece) {
						case 1:
							return Shapes.create(new AABB(zeroOffset, 0.0F, 0.0F, 1.0F, oneOffset, 1.0F));
						case 2:
							return Shapes.create(new AABB(0.0F, 0.0F, zeroOffset, 1.0F, oneOffset, 1.0F));
						}
					case WEST:
						switch (piece) {
						case 1:
							return Shapes.create(new AABB(0.0F, 0.0F, 0.0F, oneOffset, 1.0F - zeroOffset, 1.0F));
						case 2:
							return Shapes.create(new AABB(0.0F, 0.0F, zeroOffset, 1.0F, oneOffset, 1.0F));
						}
					default:
						return Shapes.empty();
					}
				} else {
					switch (state.getValue(ColorizerSlopeBlock.FACING)) {
					case NORTH:
						switch (piece) {
						case 1:
							return Shapes.create(new AABB(zeroOffset, 1.0F - oneOffset, 0.0F, 1.0F, 1.0F, 1.0F));
						case 2:
							return Shapes.create(new AABB(0.0F, zeroOffset, 0.0F, 1.0F, 1.0F, oneOffset));
						}
					case WEST:
						switch (piece) {
						case 1:
							return Shapes.create(new AABB(0.0F, zeroOffset, 0.0F, oneOffset, 1.0F, 1.0F));
						case 2:
							return Shapes.create(new AABB(0.0F, zeroOffset, 0.0F, 1.0F, 1.0F, oneOffset));
						}
					case EAST:
						switch (piece) {
						case 1:
							return Shapes.create(new AABB(zeroOffset, 1.0F - oneOffset, 0.0F, 1.0F, 1.0F, 1.0F));
						case 2:
							return Shapes.create(new AABB(0.0F, 1.0F - oneOffset, zeroOffset, 1.0F, 1.0F, 1.0F));
						}
					case SOUTH:
						switch (piece) {
						case 1:
							return Shapes.create(new AABB(0.0F, zeroOffset, 0.0F, oneOffset, 1.0F, 1.0F));
						case 2:
							return Shapes.create(new AABB(0.0F, 1.0F - oneOffset, zeroOffset, 1.0F, 1.0F, 1.0F));
						}
					default:
						return Shapes.empty();
					}
				}
			} else if (state.getBlock() == DecorBlocks.COLORIZER_CORNER.get()) {
				if (state.getValue(ColorizerSlopeBlock.HALF) == Half.BOTTOM) {
					switch (state.getValue(ColorizerSlopeBlock.FACING)) {
					case EAST:
						return Shapes.create(new AABB(zeroOffset, 0.0F, 0.0F, 1.0F, 1.0F, oneOffset));
					case NORTH:
						return Shapes.create(new AABB(0.0F, 0.0F, 0.0F, oneOffset, 1.0F, 1.0F - zeroOffset));
					case SOUTH:
						return Shapes.create(new AABB(zeroOffset, 0.0F, 1.0F - oneOffset, 1.0F, 1.0F, 1.0F));
					case WEST:
						return Shapes.create(new AABB(0.0F, 0.0F, zeroOffset, oneOffset, 1.0F, 1.0F));
					default:
						return Shapes.empty();
					}
				} else {
					switch (state.getValue(ColorizerSlopeBlock.FACING)) {
					case NORTH:
						return Shapes.create(new AABB(zeroOffset, 0.0F, 0.0F, 1.0F, 1.0F, oneOffset));
					case WEST:
						return Shapes.create(new AABB(0.0F, 0.0F, 0.0F, oneOffset, 1.0F, 1.0F - zeroOffset));
					case EAST:
						return Shapes.create(new AABB(zeroOffset, 0.0F, 1.0F - oneOffset, 1.0F, 1.0F, 1.0F));
					case SOUTH:
						return Shapes.create(new AABB(0.0F, 0.0F, zeroOffset, oneOffset, 1.0F, 1.0F));
					default:
						return Shapes.empty();
					}
				}
			}
		} else if (state.getBlock() instanceof ColorizerSlopeSideBlock) {
			if (state.getBlock() == DecorBlocks.COLORIZER_PYRAMID.get()) {
				if (state.getValue(ColorizerSlopeSideBlock.FACE) == AttachFace.CEILING) {
					return Shapes.create(new AABB(zeroOffset * 0.5F, 1.0F - oneOffset * 0.68F, zeroOffset * 0.5F, 1.0F - zeroOffset * 0.5F, 1.0F, 1.0F - zeroOffset * 0.5F));
				} else if (state.getValue(ColorizerSlopeSideBlock.FACE) == AttachFace.FLOOR) {
					return Shapes.create(new AABB(zeroOffset * 0.5F, 0.0F, zeroOffset * 0.5F, 1.0F - zeroOffset * 0.5F, oneOffset * 0.68F, 1.0F - zeroOffset * 0.5F));
				} else if (state.getValue(ColorizerSlopeSideBlock.FACE) == AttachFace.WALL) {
					switch (state.getValue(ColorizerSlopeSideBlock.HORIZONTAL_FACING)) {
					case EAST:
						return Shapes.create(new AABB(0.0F, zeroOffset * 0.5F, zeroOffset * 0.5F, oneOffset * 0.68F, 1.0F - zeroOffset * 0.5F, 1.0F - zeroOffset * 0.5F));
					case NORTH:
						return Shapes.create(new AABB(zeroOffset * 0.5F, zeroOffset * 0.5F, 1.0F - oneOffset * 0.68F, 1.0F - zeroOffset * 0.5F, 1.0F - zeroOffset * 0.5F, 1.0F));
					case SOUTH:
						return Shapes.create(new AABB(zeroOffset * 0.5F, zeroOffset * 0.5F, oneOffset * 0.68F, 1.0F - zeroOffset * 0.5F, 1.0F - zeroOffset * 0.5F, 0.0F));
					case WEST:
						return Shapes.create(new AABB(1.0F, zeroOffset * 0.5F, zeroOffset * 0.5F, 1.0F - oneOffset * 0.68F, 1.0F - zeroOffset * 0.5F, 1.0F - zeroOffset * 0.5F));
					default:
						return Shapes.empty();
					}
				}
			} else if (state.getBlock() == DecorBlocks.COLORIZER_FULL_PYRAMID.get()) {
				if (state.getValue(ColorizerSlopeSideBlock.FACE) == AttachFace.CEILING) {
					return Shapes.create(new AABB(zeroOffset * 0.5F, 1.0F - oneOffset, zeroOffset * 0.5F, 1.0F - zeroOffset * 0.5F, 1.0F, 1.0F - zeroOffset * 0.5F));
				} else if (state.getValue(ColorizerSlopeSideBlock.FACE) == AttachFace.FLOOR) {
					return Shapes.create(new AABB(zeroOffset * 0.5F, 0.0F, zeroOffset * 0.5F, 1.0F - zeroOffset * 0.5F, oneOffset, 1.0F - zeroOffset * 0.5F));
				} else if (state.getValue(ColorizerSlopeSideBlock.FACE) == AttachFace.WALL) {
					switch (state.getValue(ColorizerSlopeSideBlock.HORIZONTAL_FACING)) {
					case EAST:
						return Shapes.create(new AABB(0.0F, zeroOffset * 0.5F, zeroOffset * 0.5F, oneOffset, 1.0F - zeroOffset * 0.5F, 1.0F - zeroOffset * 0.5F));
					case NORTH:
						return Shapes.create(new AABB(zeroOffset * 0.5F, zeroOffset * 0.5F, 1.0F, 1.0F - zeroOffset * 0.5F, 1.0F - zeroOffset * 0.5F, 1.0F - oneOffset));
					case SOUTH:
						return Shapes.create(new AABB(zeroOffset * 0.5F, zeroOffset * 0.5F, 0.0F, 1.0F - zeroOffset * 0.5F, 1.0F - zeroOffset * 0.5F, oneOffset));
					case WEST:
						return Shapes.create(new AABB(1.0F, zeroOffset * 0.5F, zeroOffset * 0.5F, 1.0F - oneOffset, 1.0F - zeroOffset * 0.5F, 1.0F - zeroOffset * 0.5F));
					default:
						return Shapes.empty();
					}
				}
			} else if (state.getBlock() == DecorBlocks.COLORIZER_SLOPED_POST.get()) {
				return Shapes.block();
			}

		}

		return Shapes.empty();
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

package com.grim3212.assorted.decor.common.block;

import com.grim3212.assorted.decor.common.util.DecorUtil;
import com.grim3212.assorted.decor.common.util.DecorUtil.SlopeType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class ColorizerSlopeSideBlock extends ColorizerSideBlock {

	private final SlopeType type;

	public ColorizerSlopeSideBlock(SlopeType type) {
		this(type, Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.5f, 12.0f));
	}

	public ColorizerSlopeSideBlock(SlopeType type, Properties props) {
		super(props);
		this.type = type;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return DecorUtil.addAxisAlignedBoxes(state, worldIn, pos, context, this.type.getNumPieces());
	}

}

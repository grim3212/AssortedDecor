package com.grim3212.assorted.decor.common.blocks.colorizer;


import com.grim3212.assorted.decor.api.colorizer.SlopeType;
import com.grim3212.assorted.decor.api.util.DecorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ColorizerSlopeBlock extends ColorizerRotateBlock {

    private final SlopeType type;

    public ColorizerSlopeBlock(SlopeType type, Properties props) {
        super(props);
        this.type = type;
    }

    /**
     * The shape family this block belongs to.
     * <p>
     * {@code DecorUtil} used to tell these blocks apart by comparing them against
     * {@code DecorBlocks.COLORIZER_*.get()}. That is a registry read, and the shapes are baked
     * during block construction now, so it ran while {@code DecorBlocks}' static initialiser was
     * still going and saw a null entry. The type is instance state and needs no registry.
     */
    public SlopeType getSlopeType() {
        return this.type;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return DecorUtil.addAxisAlignedBoxes(state, worldIn, pos, context, this.type.getNumPieces());
    }

}

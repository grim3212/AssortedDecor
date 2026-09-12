package com.grim3212.assorted.decor.common.blocks.colorizer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * The colorizer shapes that are a whole block: the cube and the chimney. Only these take their
 * stored block's light dampening and shade, as in 1.20.1; a chair or a slope holding stone is still
 * mostly air, and a real stone chair casts no solid shadow.
 * <p>
 * Vanilla bakes light dampening into the block state, so the stored block's is carried in
 * {@link #LIGHT_DAMPENING}, set by the block entity when its block changes. Everything downstream is
 * then vanilla's own: the state change relights, recomputes the sky column, and reaches every client
 * as a block update, on whichever thread the light engine runs.
 */
public class ColorizerFullCubeBlock extends ColorizerBlock {

    public static final IntegerProperty LIGHT_DAMPENING = IntegerProperty.create("light_dampening", 0, 15);

    /**
     * An empty one is still a whole block, so it stops light like stone. Vanilla would bake 1 for a
     * {@code noOcclusion()} cube, which let light bleed through until a block was stored.
     */
    public static final int EMPTY_DAMPENING = 15;

    public ColorizerFullCubeBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIGHT_DAMPENING, EMPTY_DAMPENING));
    }

    /** The state {@code colorizer} should have while standing in for {@code stored}. */
    public static BlockState withStoredDampening(BlockState colorizer, BlockState stored) {
        return colorizer.setValue(LIGHT_DAMPENING, stored.isAir() ? EMPTY_DAMPENING : stored.getLightDampening());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIGHT_DAMPENING);
    }

    @Override
    protected int getLightDampening(BlockState state) {
        return state.getValue(LIGHT_DAMPENING);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return state.getValue(LIGHT_DAMPENING) == 0;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter reader, BlockPos pos) {
        BlockState stored = this.getStoredState(reader, pos);
        return stored.isAir() ? super.getShadeBrightness(state, reader, pos) : stored.getShadeBrightness(reader, pos);
    }
}

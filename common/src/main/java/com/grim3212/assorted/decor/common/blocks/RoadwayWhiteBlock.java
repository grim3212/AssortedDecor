package com.grim3212.assorted.decor.common.blocks;

import com.grim3212.assorted.decor.common.items.DecorItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class RoadwayWhiteBlock extends RoadwayColorBlock {

    public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 11);

    public RoadwayWhiteBlock(Properties props) {
        super(DyeColor.WHITE, props);
        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, 0));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(TYPE);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.getAbilities().mayBuild) {
            return InteractionResult.PASS;
        } else {
            if (player.getItemInHand(hand).getItem() == DecorItems.PAINT_ROLLER_COLORS.get(DyeColor.WHITE).get()) {
                level.setBlock(pos, state.cycle(TYPE), 3);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            return super.use(state, level, pos, player, hand, hitResult);
        }
    }
}

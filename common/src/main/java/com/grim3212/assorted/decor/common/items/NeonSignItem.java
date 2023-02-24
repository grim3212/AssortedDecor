package com.grim3212.assorted.decor.common.items;

import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.blockentity.NeonSignBlockEntity;
import com.grim3212.assorted.decor.common.network.NeonOpenPacket;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class NeonSignItem extends StandingAndWallBlockItem {

    public NeonSignItem(Item.Properties props) {
        super(DecorBlocks.NEON_SIGN.get(), DecorBlocks.NEON_SIGN_WALL.get(), props, Direction.DOWN);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level worldIn, @Nullable Player player, ItemStack stack, BlockState state) {
        boolean flag = super.updateCustomBlockEntityTag(pos, worldIn, player, stack, state);
        if (!worldIn.isClientSide && !flag && player != null) {
            ((NeonSignBlockEntity) worldIn.getBlockEntity(pos)).setOwner(player);
            Services.NETWORK.sendTo(player, new NeonOpenPacket(pos));
        }

        return flag;
    }
}
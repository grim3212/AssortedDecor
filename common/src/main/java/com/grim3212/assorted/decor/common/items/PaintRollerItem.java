package com.grim3212.assorted.decor.common.items;

import com.grim3212.assorted.decor.api.colorizer.ICanColor;
import com.grim3212.assorted.lib.annotations.LoaderImplement;
import com.grim3212.assorted.lib.util.DyeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class PaintRollerItem extends Item {
    private final DyeColor dyeColor;

    public PaintRollerItem(DyeColor color, Properties props) {
        super(props.stacksTo(1).durability(64));
        this.dyeColor = color;
    }

    @Override
    public boolean hasCraftingRemainingItem() {
        return true;
    }

    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return hasCraftingRemainingItem();
    }

    @LoaderImplement(loader = LoaderImplement.Loader.FORGE, value = "IForgeItem")
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        ItemStack copy = stack.copy();

        copy.setDamageValue(copy.getDamageValue() + 1);
        return copy.getDamageValue() >= copy.getMaxDamage() ? new ItemStack(DecorItems.PAINT_ROLLER.get()) : copy;
    }

    @LoaderImplement(loader = LoaderImplement.Loader.FABRIC, value = "FabricItem")
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return hasCraftingRemainingItem() ? getCraftingRemainingItem(stack) : ItemStack.EMPTY;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof Sheep) {
            Sheep sheep = (Sheep) entity;
            if (sheep.isAlive() && !sheep.isSheared() && sheep.getColor() != this.dyeColor) {
                sheep.level().playSound(player, sheep, SoundEvents.DYE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                if (!player.level().isClientSide) {
                    sheep.setColor(this.dyeColor);
                    player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> {
                        p.broadcastBreakEvent(hand);
                    });
                }

                return InteractionResult.sidedSuccess(player.level().isClientSide);
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        Block block = state.getBlock();

        if (block instanceof ICanColor canColor) {
            if (canColor.currentColor(state) != this.dyeColor) {
                player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> {
                    p.broadcastBreakEvent(hand);
                });
                level.setBlock(pos, canColor.stateForColor(state, this.dyeColor), 3);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        Map<DyeColor, Block> match = isBlockFound(block);
        if (match != null) {
            Optional<DyeColor> curColor = match.entrySet().stream().filter(entry -> block.equals(entry.getValue())).map(Map.Entry::getKey).findFirst();
            if (curColor.isPresent() && curColor.get() != this.dyeColor) {
                player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> {
                    p.broadcastBreakEvent(hand);
                });
                level.setBlock(pos, match.getOrDefault(this.dyeColor, block).defaultBlockState(), 3);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return super.useOn(context);
    }

    @Nullable
    private Map<DyeColor, Block> isBlockFound(Block b) {
        Optional<Map<DyeColor, Block>> containsMatch = DyeHelper.BLOCKS_BY_DYE.stream().filter((x) -> x.containsValue(b)).findFirst();
        if (containsMatch.isPresent()) {
            return containsMatch.get();
        }

        return null;
    }

    public DyeColor getDyeColor() {
        return this.dyeColor;
    }
}

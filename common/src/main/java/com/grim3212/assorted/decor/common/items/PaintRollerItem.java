package com.grim3212.assorted.decor.common.items;

import com.grim3212.assorted.decor.api.colorizer.ICanColor;
import com.grim3212.assorted.lib.annotations.LoaderImplement;
import com.grim3212.assorted.lib.util.DyeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
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

    /**
     * Item.Properties#craftRemainder only takes a fixed ItemStackTemplate, which cannot express
     * "the same roller, one point more worn". Both loaders still ask the item per stack, and both
     * now expect an ItemStackTemplate back, so the wear rides along as a component patch.
     */
    @LoaderImplement(loader = LoaderImplement.Loader.FORGE, value = "IItemExtension")
    public ItemStackTemplate getCraftingRemainder(ItemInstance stack) {
        int damage = stack.getOrDefault(DataComponents.DAMAGE, 0) + 1;
        int maxDamage = stack.getOrDefault(DataComponents.MAX_DAMAGE, 0);

        if (maxDamage > 0 && damage >= maxDamage) {
            return new ItemStackTemplate(DecorItems.PAINT_ROLLER.get());
        }

        return new ItemStackTemplate(stack.typeHolder(), 1, DataComponentPatch.builder().set(DataComponents.DAMAGE, damage).build());
    }

    @LoaderImplement(loader = LoaderImplement.Loader.FABRIC, value = "FabricItem")
    public ItemStackTemplate getCraftingRemainder(ItemStack stack) {
        return this.getCraftingRemainder((ItemInstance) stack);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof Sheep) {
            Sheep sheep = (Sheep) entity;
            if (sheep.isAlive() && !sheep.isSheared() && sheep.getColor() != this.dyeColor) {
                sheep.level().playSound(player, sheep, SoundEvents.DYE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                if (!player.level().isClientSide()) {
                    sheep.setColor(this.dyeColor);
                    player.getItemInHand(hand).hurtAndBreak(1, player, hand.asEquipmentSlot());
                }

                return InteractionResult.SUCCESS;
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
                player.getItemInHand(hand).hurtAndBreak(1, player, hand.asEquipmentSlot());
                level.setBlock(pos, canColor.stateForColor(state, this.dyeColor), 3);
                return InteractionResult.SUCCESS;
            }
        }

        Map<DyeColor, Block> match = isBlockFound(block);
        if (match != null) {
            Optional<DyeColor> curColor = match.entrySet().stream().filter(entry -> block.equals(entry.getValue())).map(Map.Entry::getKey).findFirst();
            if (curColor.isPresent() && curColor.get() != this.dyeColor) {
                player.getItemInHand(hand).hurtAndBreak(1, player, hand.asEquipmentSlot());
                level.setBlock(pos, match.getOrDefault(this.dyeColor, block).defaultBlockState(), 3);
                return InteractionResult.SUCCESS;
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

package com.grim3212.assorted.decor.gametest;

import com.grim3212.assorted.decor.common.blocks.ColorChangingBlock;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.lib.util.DyeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.decor.gametest.DecorTestSupport.*;

/**
 * Paint rollers and siding: recolouring blocks and sheep, and colour kept on the item.
 */
final class PaintTests {

    private PaintTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("paint_roller_recolors_blocks", PaintTests::paintRollerRecolorsBlocks);
        out.accept("siding_item_keeps_color", PaintTests::sidingItemKeepsColor);
        out.accept("paint_rollers_recolor_every_color", PaintTests::paintRollersRecolorEveryColor);
        out.accept("paint_roller_dyes_a_sheep", PaintTests::paintRollerDyesASheep);
    }

    /**
     * A paint roller repaints both kinds of target: a vanilla dyed block, which it swaps for the
     * matching block of its own colour, and an {@code ICanColor} block, which it recolours in place
     * through {@code stateForColor}.
     */
    private static void paintRollerRecolorsBlocks(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos wool = new BlockPos(2, 1, 4);
        BlockPos siding = new BlockPos(6, 1, 4);

        helper.setBlock(wool, DyeHelper.WOOL_BY_DYE.get(DyeColor.WHITE));
        helper.setBlock(siding, DecorBlocks.SIDING_VERTICAL.get());

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack roller = new ItemStack(DecorItems.PAINT_ROLLER_COLORS.get(DyeColor.BLUE).get());
        player.setItemInHand(InteractionHand.MAIN_HAND, roller);

        roller.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(wool))));
        helper.assertBlockPresent(DyeHelper.WOOL_BY_DYE.get(DyeColor.BLUE), wool);

        helper.assertTrue(DecorBlocks.SIDING_VERTICAL.get().currentColor(level.getBlockState(helper.absolutePos(siding))) == DyeColor.WHITE,
                "siding did not start out white");
        roller.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(siding))));
        helper.assertBlockProperty(siding, ColorChangingBlock.COLOR, DyeColor.BLUE);
        helper.succeed();
    }

    /**
     * A dyed siding item places as that colour: the {@code DataComponents.BLOCK_STATE} the mod
     * writes is what {@code BlockItem} applies. Both sidings, in two colours, so neither can pass
     * by accident.
     */
    private static void sidingItemKeepsColor(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        placeSidingFromItem(helper, player, new BlockPos(3, 1, 4), DecorBlocks.SIDING_HORIZONTAL.get(), DyeColor.RED);
        placeSidingFromItem(helper, player, new BlockPos(5, 1, 4), DecorBlocks.SIDING_VERTICAL.get(), DyeColor.LIME);

        helper.succeed();
    }

    /**
     * All sixteen rollers, against both kinds of vanilla dyed block the roller knows about.
     * {@code DyeHelper.BLOCKS_BY_DYE} is the whole list it searches, and it holds wool, concrete,
     * concrete powder and carpet - not terracotta, despite what the checklist used to claim.
     */
    private static void paintRollersRecolorEveryColor(GameTestHelper helper) {
        BlockPos wool = new BlockPos(2, 1, 4);
        BlockPos concrete = new BlockPos(6, 1, 4);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        for (DyeColor color : DyeColor.values()) {
            // A roller does nothing to a block already its own colour, so start somewhere else.
            DyeColor from = color == DyeColor.WHITE ? DyeColor.BLACK : DyeColor.WHITE;
            helper.setBlock(wool, DyeHelper.WOOL_BY_DYE.get(from));
            helper.setBlock(concrete, DyeHelper.CONCRETE_BY_DYE.get(from));

            ItemStack roller = new ItemStack(DecorItems.PAINT_ROLLER_COLORS.get(color).get());
            player.setItemInHand(InteractionHand.MAIN_HAND, roller);

            roller.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(wool))));
            roller.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(concrete))));

            helper.assertBlockPresent(DyeHelper.WOOL_BY_DYE.get(color), wool);
            helper.assertBlockPresent(DyeHelper.CONCRETE_BY_DYE.get(color), concrete);
            helper.assertValueEqual(roller.getDamageValue(), 2, "durability the " + color.getName() + " roller spent on two blocks");
        }

        helper.succeed();
    }

    /** A roller dyes a sheep the same way a dye would, and wears by one for it. */
    private static void paintRollerDyesASheep(GameTestHelper helper) {
        Sheep sheep = helper.spawn(EntityTypes.SHEEP, MAIN);
        sheep.setColor(DyeColor.WHITE);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack roller = new ItemStack(DecorItems.PAINT_ROLLER_COLORS.get(DyeColor.RED).get());
        player.setItemInHand(InteractionHand.MAIN_HAND, roller);

        helper.assertTrue(roller.getItem().interactLivingEntity(roller, player, sheep, InteractionHand.MAIN_HAND).consumesAction(),
                "the roller did not handle a sheep");
        helper.assertTrue(sheep.getColor() == DyeColor.RED, "the roller did not dye the sheep");
        helper.assertValueEqual(roller.getDamageValue(), 1, "durability the roller spent on a sheep");
        helper.succeed();
    }
}

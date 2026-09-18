package com.grim3212.assorted.decor.gametest;

import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.GateBlock;
import com.grim3212.assorted.decor.common.items.DecorItems;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;

/**
 * The castle gate and garage door: hanging from a ceiling and filling down, opening as one with the
 * columns beside them, redstone, and coming down as a single item.
 */
final class GateTests {

    /** Stone ceilings at this height hang gates in y 1 to 4, down to the test box floor. */
    private static final int CEILING = 5;

    private GateTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("gate_fills_down_from_a_ceiling", GateTests::gateFillsDownFromACeiling);
        out.accept("gate_needs_a_ceiling", GateTests::gateNeedsACeiling);
        out.accept("gate_opens_as_one_with_the_columns_beside_it", GateTests::gateOpensAsOneWithTheColumnsBesideIt);
        out.accept("gate_activator_works_from_a_distance", GateTests::gateActivatorWorksFromADistance);
        out.accept("gate_activator_works_looking_from_the_side", GateTests::gateActivatorWorksLookingFromTheSide);
        out.accept("gate_redstone_holds_it_open", GateTests::gateRedstoneHoldsItOpen);
        out.accept("breaking_a_gate_block_takes_the_column_and_drops_one", GateTests::breakingAGateBlockTakesTheColumnAndDropsOne);
        out.accept("gate_comes_down_with_its_ceiling", GateTests::gateComesDownWithItsCeiling);
        out.accept("open_gate_leaves_a_doorway_to_build_in", GateTests::openGateLeavesADoorwayToBuildIn);
        out.accept("closing_gate_fills_the_gaps_below_it", GateTests::closingGateFillsTheGapsBelowIt);
    }

    /** Places a gate by hand against the underside of a stone block over {@code x, z}. */
    private static ServerPlayer hang(GameTestHelper helper, GateBlock gate, int x, int z) {
        BlockPos ceiling = new BlockPos(x, CEILING, z);
        helper.setBlock(ceiling, Blocks.STONE);
        ServerPlayer player = survivalPlayer(helper, new ItemStack(gate));
        stand(helper, player, new BlockPos(x, 0, 1));
        player.getMainHandItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitSide(helper.absolutePos(ceiling), Direction.DOWN)));
        return player;
    }

    /** A closed column from the ceiling to the floor, or an open one: its top block and air beneath. */
    private static void assertColumn(GameTestHelper helper, GateBlock gate, int x, int z, boolean open) {
        assertColumn(helper, gate, x, z, open, 1);
    }

    private static void assertColumn(GameTestHelper helper, GateBlock gate, int x, int z, boolean open, int bottom) {
        BlockPos top = new BlockPos(x, CEILING - 1, z);
        helper.assertBlockPresent(gate, top);
        helper.assertBlockProperty(top, GateBlock.OPEN, open);
        helper.assertBlockProperty(top, GateBlock.TOP, true);
        for (int y = bottom; y < CEILING - 1; y++) {
            BlockPos pos = new BlockPos(x, y, z);
            if (open) {
                helper.assertBlockPresent(Blocks.AIR, pos);
            } else {
                helper.assertBlockPresent(gate, pos);
                helper.assertBlockProperty(pos, GateBlock.OPEN, false);
                helper.assertBlockProperty(pos, GateBlock.TOP, false);
            }
        }
    }

    private static ServerPlayer trumpeter(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper, new ItemStack(DecorItems.GATE_TRUMPET.get()));
        stand(helper, player, new BlockPos(1, 0, 1));
        return player;
    }

    /** Right clicks the gate block at {@code rel}, clearing the trumpet's cooldown first. */
    private static void blow(GameTestHelper helper, ServerPlayer player, BlockPos rel) {
        player.getCooldowns().removeCooldown(player.getCooldowns().getCooldownGroup(player.getMainHandItem()));
        rightClick(player, helper.getLevel(), player.getMainHandItem(), helper.absolutePos(rel));
    }

    private static void gateFillsDownFromACeiling(GameTestHelper helper) {
        GateBlock gate = DecorBlocks.CASTLE_GATE.get();
        ServerPlayer player = hang(helper, gate, 4, 4);

        assertColumn(helper, gate, 4, 4, false);
        helper.assertTrue(player.getMainHandItem().isEmpty(), "hanging the gate did not use the item");
        BlockState closed = helper.getBlockState(new BlockPos(4, 2, 4));
        helper.assertFalse(closed.getCollisionShape(helper.getLevel(), helper.absolutePos(new BlockPos(4, 2, 4)), CollisionContext.empty()).isEmpty(), "a closed gate can be walked through");

        helper.succeed();
    }

    private static void gateNeedsACeiling(GameTestHelper helper) {
        helper.setBlock(new BlockPos(4, 1, 4), Blocks.STONE);
        ServerPlayer player = survivalPlayer(helper, new ItemStack(DecorBlocks.GARAGE_DOOR.get()));
        stand(helper, player, new BlockPos(4, 0, 2));

        useOnTopOf(helper, player, new BlockPos(4, 1, 4));
        helper.assertBlockNotPresent(DecorBlocks.GARAGE_DOOR.get(), new BlockPos(4, 2, 4));
        helper.assertFalse(player.getMainHandItem().isEmpty(), "a garage door was used up with nothing to hang from");

        helper.succeed();
    }

    /**
     * Two columns side by side open and close together from one click. Open, only the top block of
     * each stays solid, and only its upper half.
     */
    private static void gateOpensAsOneWithTheColumnsBesideIt(GameTestHelper helper) {
        GateBlock gate = DecorBlocks.CASTLE_GATE.get();
        ServerPlayer player = hang(helper, gate, 3, 4);
        hang(helper, gate, 4, 4);

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(DecorItems.GATE_TRUMPET.get()));
        rightClick(player, helper.getLevel(), player.getMainHandItem(), helper.absolutePos(new BlockPos(3, 2, 4)));
        assertColumn(helper, gate, 3, 4, true);
        assertColumn(helper, gate, 4, 4, true);

        BlockPos lower = new BlockPos(4, 2, 4);
        BlockPos top = new BlockPos(4, CEILING - 1, 4);
        helper.assertBlockPresent(Blocks.AIR, lower);
        helper.assertValueEqual(helper.getBlockState(top).getCollisionShape(helper.getLevel(), helper.absolutePos(top), CollisionContext.empty()).min(Direction.Axis.Y), 7.0D / 16.0D, "the bottom of an open gate's top block");

        // Straight away the trumpet is still sounding, and does nothing.
        rightClick(player, helper.getLevel(), player.getMainHandItem(), helper.absolutePos(top));
        assertColumn(helper, gate, 3, 4, true);
        helper.assertTrue(player.getCooldowns().isOnCooldown(player.getMainHandItem()), "the trumpet has no cooldown after use");

        player.getCooldowns().removeCooldown(player.getCooldowns().getCooldownGroup(player.getMainHandItem()));
        rightClick(player, helper.getLevel(), player.getMainHandItem(), helper.absolutePos(top));
        assertColumn(helper, gate, 3, 4, false);
        assertColumn(helper, gate, 4, 4, false);

        // The garage remote does nothing to a castle gate.
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(DecorItems.GARAGE_REMOTE.get()));
        rightClick(player, helper.getLevel(), player.getMainHandItem(), helper.absolutePos(lower));
        assertColumn(helper, gate, 4, 4, false);

        helper.succeed();
    }

    /** Used in the air, the remote works the first garage door ahead of the player. */
    private static void gateActivatorWorksFromADistance(GameTestHelper helper) {
        GateBlock door = DecorBlocks.GARAGE_DOOR.get();
        hang(helper, door, 4, 7);

        ServerPlayer player = survivalPlayer(helper, new ItemStack(DecorItems.GARAGE_REMOTE.get()));
        stand(helper, player, new BlockPos(4, 0, 1));
        player.setYRot(0.0F);
        player.getMainHandItem().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        assertColumn(helper, door, 4, 7, true);

        // And again to close it, now that only its retracted top is left to find.
        player.getCooldowns().removeCooldown(player.getCooldowns().getCooldownGroup(player.getMainHandItem()));
        player.getMainHandItem().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        assertColumn(helper, door, 4, 7, false);

        helper.succeed();
    }

    /**
     * Used in the air from off to one side, the remote follows where the player is looking rather
     * than the compass direction they face: a door diagonally away is found, and a straight line
     * along their facing would have missed it.
     */
    private static void gateActivatorWorksLookingFromTheSide(GameTestHelper helper) {
        GateBlock door = DecorBlocks.GARAGE_DOOR.get();
        hang(helper, door, 7, 7);

        ServerPlayer player = survivalPlayer(helper, new ItemStack(DecorItems.GARAGE_REMOTE.get()));
        stand(helper, player, new BlockPos(1, 0, 2));
        player.lookAt(EntityAnchorArgument.Anchor.EYES, helper.absoluteVec(new Vec3(7.5D, 2.5D, 7.5D)));
        player.getMainHandItem().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        assertColumn(helper, door, 7, 7, true);

        // Open, only the doorway is left along the same look, and that still finds it.
        player.getCooldowns().removeCooldown(player.getCooldowns().getCooldownGroup(player.getMainHandItem()));
        player.getMainHandItem().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        assertColumn(helper, door, 7, 7, false);

        helper.succeed();
    }

    /**
     * Power at the top block opens the gate and holds it open, and losing it closes the gate. Power
     * down in the doorway does nothing.
     */
    private static void gateRedstoneHoldsItOpen(GameTestHelper helper) {
        GateBlock gate = DecorBlocks.CASTLE_GATE.get();
        hang(helper, gate, 4, 4);

        helper.setBlock(new BlockPos(5, 1, 4), Blocks.REDSTONE_BLOCK);
        assertColumn(helper, gate, 4, 4, false);

        helper.setBlock(new BlockPos(5, CEILING - 1, 4), Blocks.REDSTONE_BLOCK);
        assertColumn(helper, gate, 4, 4, true);
        helper.setBlock(new BlockPos(5, CEILING - 1, 4), Blocks.AIR);
        assertColumn(helper, gate, 4, 4, false);

        helper.succeed();
    }

    /** An open gate leaves an empty doorway; what is built in it is where the gate stops when it closes. */
    private static void openGateLeavesADoorwayToBuildIn(GameTestHelper helper) {
        GateBlock gate = DecorBlocks.CASTLE_GATE.get();
        hang(helper, gate, 4, 4);
        ServerPlayer player = trumpeter(helper);
        blow(helper, player, new BlockPos(4, 2, 4));
        assertColumn(helper, gate, 4, 4, true);

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Blocks.STONE));
        useOnTopOf(helper, player, new BlockPos(4, 0, 4));
        helper.assertBlockPresent(Blocks.STONE, new BlockPos(4, 1, 4));

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(DecorItems.GATE_TRUMPET.get()));
        blow(helper, player, new BlockPos(4, CEILING - 1, 4));
        assertColumn(helper, gate, 4, 4, false, 2);
        helper.assertBlockPresent(Blocks.STONE, new BlockPos(4, 1, 4));

        helper.succeed();
    }

    /** What was broken out from under a gate is filled again when it next closes. */
    private static void closingGateFillsTheGapsBelowIt(GameTestHelper helper) {
        GateBlock gate = DecorBlocks.CASTLE_GATE.get();
        helper.setBlock(new BlockPos(4, 1, 4), Blocks.STONE);
        hang(helper, gate, 4, 4);
        assertColumn(helper, gate, 4, 4, false, 2);

        ServerPlayer player = trumpeter(helper);
        blow(helper, player, new BlockPos(4, 2, 4));
        helper.setBlock(new BlockPos(4, 1, 4), Blocks.AIR);
        blow(helper, player, new BlockPos(4, CEILING - 1, 4));
        assertColumn(helper, gate, 4, 4, false);

        helper.succeed();
    }

    private static void breakingAGateBlockTakesTheColumnAndDropsOne(GameTestHelper helper) {
        GateBlock gate = DecorBlocks.CASTLE_GATE.get();
        ServerPlayer player = hang(helper, gate, 4, 4);

        player.gameMode.destroyBlock(helper.absolutePos(new BlockPos(4, 2, 4)));
        for (int y = 1; y < CEILING; y++) {
            helper.assertBlockNotPresent(gate, new BlockPos(4, y, 4));
        }
        helper.assertValueEqual(helper.getEntities(EntityTypes.ITEM).stream().mapToInt(item -> item.getItem().is(gate.asItem()) ? item.getItem().getCount() : 0).sum(), 1, "castle gates dropped for one column");

        helper.succeed();
    }

    private static void gateComesDownWithItsCeiling(GameTestHelper helper) {
        GateBlock door = DecorBlocks.GARAGE_DOOR.get();
        hang(helper, door, 4, 4);

        helper.setBlock(new BlockPos(4, CEILING, 4), Blocks.AIR);
        for (int y = 1; y < CEILING; y++) {
            helper.assertBlockNotPresent(door, new BlockPos(4, y, 4));
        }
        helper.assertValueEqual(helper.getEntities(EntityTypes.ITEM).stream().mapToInt(item -> item.getItem().is(door.asItem()) ? item.getItem().getCount() : 0).sum(), 1, "garage doors dropped for one column");

        helper.succeed();
    }
}

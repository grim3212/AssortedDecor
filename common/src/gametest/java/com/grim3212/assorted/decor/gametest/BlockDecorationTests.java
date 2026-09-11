package com.grim3212.assorted.decor.gametest;

import net.minecraft.server.level.ServerPlayer;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.decor.common.items.NeonSignItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.nbt.NbtOps;
import net.minecraft.core.component.DataComponents;
import com.grim3212.assorted.decor.common.blocks.blockentity.DecorBlockEntityTypes;
import com.grim3212.assorted.decor.api.util.DateHandler;
import com.grim3212.assorted.decor.common.blocks.BoneDecorationBlock;
import com.grim3212.assorted.decor.common.blocks.ClayDecorationBlock;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.PlanterPotBlock;
import com.grim3212.assorted.decor.common.blocks.RoadwayManholeBlock;
import com.grim3212.assorted.decor.common.blocks.RoadwayWhiteBlock;
import com.grim3212.assorted.decor.common.blocks.blockentity.CalendarBlockEntity;
import com.grim3212.assorted.decor.common.blocks.blockentity.NeonSignBlockEntity;
import com.grim3212.assorted.decor.common.items.DecorItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.decor.gametest.DecorTestSupport.*;

/**
 * Decorative blocks: roadways, fences, doors, the calendar, planter pots, decoration blocks and the neon sign.
 */
final class BlockDecorationTests {

    private BlockDecorationTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("roadway_blocks_place_and_react", BlockDecorationTests::roadwayBlocksPlaceAndReact);
        out.accept("chain_link_fence_connects", BlockDecorationTests::chainLinkFenceConnects);
        out.accept("doors_open_on_redstone", BlockDecorationTests::doorsOpenOnRedstone);
        out.accept("calendar_shows_the_date", BlockDecorationTests::calendarShowsTheDate);
        out.accept("planter_pot_holds_a_plant", BlockDecorationTests::planterPotHoldsAPlant);
        out.accept("decoration_blocks_place", BlockDecorationTests::decorationBlocksPlace);
        out.accept("neon_sign_text_survives_reload", BlockDecorationTests::neonSignTextSurvivesReload);
        out.accept("neon_sign_item_data_needs_an_operator", BlockDecorationTests::neonSignItemDataNeedsAnOperator);
    }

    /**
     * Every road surface places from its item, and the three that react to being used react.
     * <p>
     * There is nothing to assert about roadways "connecting": they carry no connection properties
     * at all, their textures simply tile. What the block states do carry is the manhole's
     * {@code open}, the white roadway's marking {@code type} and the colour swap a roller does, and
     * those are what is checked here.
     */
    private static void roadwayBlocksPlaceAndReact(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        List<Block> road = List.of(DecorBlocks.ROADWAY.get(), DecorBlocks.ROADWAY_COLORS.get(DyeColor.WHITE).get(), DecorBlocks.ROADWAY_LIGHT.get(),
                DecorBlocks.ROADWAY_MANHOLE.get(), DecorBlocks.SIDEWALK.get(), DecorBlocks.STONE_PATH.get());

        for (int i = 0; i < road.size(); i++) {
            BlockPos floor = new BlockPos(1 + i, 1, 2);
            helper.setBlock(floor, Blocks.STONE);

            ItemStack stack = new ItemStack(road.get(i));
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(floor))));

            helper.assertBlockPresent(road.get(i), floor.above());
        }

        BlockPos plain = new BlockPos(1, 2, 2);
        BlockPos white = new BlockPos(2, 2, 2);
        BlockPos manhole = new BlockPos(4, 2, 2);

        // The manhole answers an empty hand, so it goes through useBlock - the real interaction path.
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        helper.assertBlockProperty(manhole, RoadwayManholeBlock.OPEN, false);
        helper.useBlock(manhole, player, hitTop(helper.absolutePos(manhole)));
        helper.assertBlockProperty(manhole, RoadwayManholeBlock.OPEN, true);
        helper.useBlock(manhole, player, hitTop(helper.absolutePos(manhole)));
        helper.assertBlockProperty(manhole, RoadwayManholeBlock.OPEN, false);

        // The white roadway cycles its marking under a white roller, which the block itself handles
        // in useItemOn - so this one has to go through useBlock too, not through the item.
        ItemStack whiteRoller = new ItemStack(DecorItems.PAINT_ROLLER_COLORS.get(DyeColor.WHITE).get());
        player.setItemInHand(InteractionHand.MAIN_HAND, whiteRoller);
        helper.assertBlockProperty(white, RoadwayWhiteBlock.TYPE, 0);
        helper.useBlock(white, player, hitTop(helper.absolutePos(white)));
        helper.assertBlockProperty(white, RoadwayWhiteBlock.TYPE, 1);

        // Plain roadway is repainted by the item instead, through ICanColor.
        ItemStack redRoller = new ItemStack(DecorItems.PAINT_ROLLER_COLORS.get(DyeColor.RED).get());
        player.setItemInHand(InteractionHand.MAIN_HAND, redRoller);
        redRoller.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(plain))));
        helper.assertBlockPresent(DecorBlocks.ROADWAY_COLORS.get(DyeColor.RED).get(), plain);

        helper.succeed();
    }

    /** The chain link fence joins up with its neighbours, and only with the sides that have one. */
    private static void chainLinkFenceConnects(GameTestHelper helper) {
        Block fence = DecorBlocks.CHAIN_LINK_FENCE.get();

        helper.setBlock(MAIN, fence);
        for (var side : List.of(CrossCollisionBlock.NORTH, CrossCollisionBlock.EAST, CrossCollisionBlock.SOUTH, CrossCollisionBlock.WEST)) {
            helper.assertBlockProperty(MAIN, side, false);
        }

        helper.setBlock(MAIN.north(), fence);
        helper.setBlock(MAIN.east(), fence);

        helper.assertBlockProperty(MAIN, CrossCollisionBlock.NORTH, true);
        helper.assertBlockProperty(MAIN, CrossCollisionBlock.EAST, true);
        helper.assertBlockProperty(MAIN, CrossCollisionBlock.SOUTH, false);
        helper.assertBlockProperty(MAIN, CrossCollisionBlock.WEST, false);
        helper.succeed();
    }

    /**
     * The four plain doors place as two halves and open together under redstone.
     * <p>
     * Redstone and not a right click on purpose: {@code DecorDoorBlock} passes
     * {@link net.minecraft.world.level.block.state.properties.BlockSetType#IRON}, whose
     * {@code canOpenByHand()} is false, so {@code DoorBlock#useWithoutItem} returns straight away.
     * These doors have always behaved like iron doors; the checklist just did not say so.
     */
    private static void doorsOpenOnRedstone(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        List<Block> doors = List.of(DecorBlocks.CHAIN_LINK_DOOR.get(), DecorBlocks.GLASS_DOOR.get(), DecorBlocks.QUARTZ_DOOR.get(), DecorBlocks.STEEL_DOOR.get());

        for (int i = 0; i < doors.size(); i++) {
            Block door = doors.get(i);
            String name = BuiltInRegistries.BLOCK.getKey(door).getPath();
            BlockPos floor = new BlockPos(1 + i * 2, 1, 4);
            BlockPos lower = floor.above();
            BlockPos upper = lower.above();

            helper.setBlock(floor, Blocks.STONE);
            ItemStack stack = new ItemStack(door);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(floor))));

            helper.assertBlockPresent(door, lower);
            helper.assertBlockPresent(door, upper);
            helper.assertBlockProperty(lower, DoorBlock.HALF, DoubleBlockHalf.LOWER);
            helper.assertBlockProperty(upper, DoorBlock.HALF, DoubleBlockHalf.UPPER);
            helper.assertBlockProperty(lower, DoorBlock.OPEN, false);

            helper.setBlock(lower.west(), Blocks.REDSTONE_BLOCK);
            helper.assertBlockProperty(lower, DoorBlock.OPEN, true);
            helper.assertBlockProperty(upper, DoorBlock.OPEN, true);

            helper.setBlock(lower.west(), Blocks.AIR);
            helper.assertBlockProperty(lower, DoorBlock.OPEN, false);
            helper.assertBlockProperty(upper, DoorBlock.OPEN, false);

            helper.assertTrue(name.endsWith("door"), name + " is in the door list but is not a door");
        }

        helper.succeed();
    }

    /**
     * The calendar hangs on a wall with its block entity, and the date it renders is right.
     * <p>
     * {@link DateHandler#calculateDate} is pure arithmetic over the world time, so it can simply be
     * asserted. The suffixes are the part worth pinning: {@code ordinalNo} has to get the teens
     * right, which is exactly the case a naive last-digit switch gets wrong.
     */
    private static void calendarShowsTheDate(GameTestHelper helper) {
        BlockPos wall = new BlockPos(4, 2, 5);
        BlockPos hanging = new BlockPos(4, 2, 4);

        helper.setBlock(wall, Blocks.STONE);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack stack = new ItemStack(DecorBlocks.CALENDAR.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitSide(helper.absolutePos(wall), Direction.NORTH)));

        helper.assertBlockPresent(DecorBlocks.CALENDAR.get(), hanging);
        helper.assertBlockProperty(hanging, HorizontalDirectionalBlock.FACING, Direction.NORTH);
        helper.getBlockEntity(hanging, CalendarBlockEntity.class);

        // Day one is the first day of the first year; a day is 24000 ticks and the year is 365 days.
        helper.assertValueEqual(DateHandler.calculateDate(0L, 0), "1st January, Year 1", "the date on day one");
        helper.assertValueEqual(DateHandler.calculateDate(0L, 1), "Year 1,Jan 1,Mon", "the short date on day one");
        helper.assertValueEqual(DateHandler.calculateDate(10L * 24000L, 0), "11th January, Year 1", "the date on day eleven");
        helper.assertValueEqual(DateHandler.calculateDate(20L * 24000L, 0), "21st January, Year 1", "the date on day twenty one");
        helper.assertValueEqual(DateHandler.calculateDate(31L * 24000L, 0), "1st February, Year 1", "the date the month rolls over");
        helper.assertValueEqual(DateHandler.calculateDate(365L * 24000L, 0), "1st January, Year 2", "the date the year rolls over");

        for (Map.Entry<Integer, String> expected : Map.of(1, "st", 2, "nd", 3, "rd", 4, "th", 11, "th", 12, "th", 13, "th", 21, "st", 22, "nd", 23, "rd").entrySet()) {
            helper.assertValueEqual(DateHandler.ordinalNo(expected.getKey()), expected.getValue(), "the suffix on " + expected.getKey());
        }

        helper.succeed();
    }

    /**
     * A planter pot sustains a plant its soil setting allows and refuses one it does not. The hook
     * is the library's {@code IPlantSustainable}, reached through a mixin on vanilla's
     * {@code VegetationBlock#mayPlaceOn} - so this is really a check that the mixin still applies.
     */
    private static void planterPotHoldsAPlant(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pot = MAIN;
        BlockPos plant = helper.absolutePos(MAIN.above());

        helper.setBlock(pot, DecorBlocks.PLANTER_POT.get());
        helper.assertBlockProperty(pot, PlanterPotBlock.TOP, 0);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack poppy = new ItemStack(Items.POPPY);
        player.setItemInHand(InteractionHand.MAIN_HAND, poppy);
        poppy.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(pot))));

        helper.assertBlockPresent(Blocks.POPPY, MAIN.above());
        helper.assertTrue(level.getBlockState(plant).canSurvive(level, plant), "a poppy in a planter pot did not think it could survive there");

        // Soil 2 is the bamboo setting, which a flower has no business growing in.
        helper.setBlock(pot, DecorBlocks.PLANTER_POT.get().defaultBlockState().setValue(PlanterPotBlock.TOP, 2));
        helper.assertFalse(Blocks.POPPY.defaultBlockState().canSurvive(level, plant), "the planter pot sustained a flower on its bamboo soil");
        helper.succeed();
    }

    /**
     * The plain decorations place from their items, and asphalt does what it does instead of
     * placing: it turns a block of {@code c:stones} into roadway under itself.
     */
    private static void decorationBlocksPlace(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        BlockPos stone = new BlockPos(1, 1, 4);
        helper.setBlock(stone, Blocks.STONE);
        ItemStack asphalt = new ItemStack(DecorItems.ASPHALT.get(), 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, asphalt);
        asphalt.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(stone))));

        helper.assertBlockPresent(DecorBlocks.ROADWAY.get(), stone);
        helper.assertValueEqual(asphalt.getCount(), 1, "asphalt left after surfacing one block");

        List<Block> plain = List.of(DecorBlocks.DECORATIVE_STONE.get(), DecorBlocks.CLAY_DECORATION.get(), DecorBlocks.BONE_DECORATION.get());
        for (int i = 0; i < plain.size(); i++) {
            BlockPos floor = new BlockPos(3 + i, 1, 4);
            helper.setBlock(floor, Blocks.STONE);

            ItemStack stack = new ItemStack(plain.get(i));
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(floor))));

            helper.assertBlockPresent(plain.get(i), floor.above());
        }

        // Both decorations cycle through their variants when used, which is the only state they have.
        BlockPos clay = new BlockPos(4, 2, 4);
        BlockPos bone = new BlockPos(5, 2, 4);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

        helper.assertBlockProperty(clay, ClayDecorationBlock.DECORATION, 0);
        helper.useBlock(clay, player, hitTop(helper.absolutePos(clay)));
        helper.assertBlockProperty(clay, ClayDecorationBlock.DECORATION, 1);

        helper.assertBlockProperty(bone, BoneDecorationBlock.DECORATION, 0);
        helper.useBlock(bone, player, hitTop(helper.absolutePos(bone)));
        helper.assertBlockProperty(bone, BoneDecorationBlock.DECORATION, 1);

        helper.succeed();
    }

    /**
     * Neon sign text and mode survive being written out and read back. The four lines moved onto
     * {@code ComponentSerialization.CODEC} through {@code ValueInput}/{@code ValueOutput} in the
     * port, and {@code loadAdditional} resolves each line against a command source on the way in -
     * a step that has to cope with a block entity that has no level yet.
     */
    private static void neonSignTextSurvivesReload(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(MAIN);

        helper.setBlock(MAIN, DecorBlocks.NEON_SIGN.get());
        NeonSignBlockEntity sign = helper.getBlockEntity(MAIN, NeonSignBlockEntity.class);

        sign.mode = 2;
        for (int i = 0; i < 4; i++) {
            sign.setText(i, Component.literal("line " + (i + 1)));
        }

        CompoundTag saved = sign.saveWithFullMetadata(level.registryAccess());
        BlockEntity reloaded = BlockEntity.loadStatic(pos, sign.getBlockState(), saved, level.registryAccess());

        helper.assertTrue(reloaded instanceof NeonSignBlockEntity, "a saved neon sign did not load back as a neon sign");
        NeonSignBlockEntity loaded = (NeonSignBlockEntity) reloaded;

        helper.assertValueEqual(loaded.mode, 2, "neon sign mode after a save/load round trip");
        for (int i = 0; i < 4; i++) {
            helper.assertValueEqual(loaded.getText(i).getString(), "line " + (i + 1), "neon sign line " + (i + 1) + " after a save/load round trip");
        }

        helper.succeed();
    }

    /**
     * Block entity data on a neon sign item is applied for an operator only, as vanilla does for a
     * sign. Without the check anyone could place a neon sign with any text or owner.
     * <p>
     * The rule is asked on both loaders. The real placement by a non-operator runs on Fabric only:
     * placing as a player opens the editor with a packet, and NeoForge refuses to send it to a test
     * player. The placement code is common, so the Fabric run covers it for both.
     */
    private static void neonSignItemDataNeedsAnOperator(GameTestHelper helper) {
        CompoundTag data = new CompoundTag();
        data.put("Text1", ComponentSerialization.CODEC.encodeStart(NbtOps.INSTANCE, Component.literal("from an item")).getOrThrow());
        ItemStack sign = new ItemStack(DecorItems.NEON_SIGN.get());
        sign.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.<BlockEntityType<?>>of(DecorBlockEntityTypes.NEON_SIGN.get(), data));

        Player mock = helper.makeMockPlayer(GameType.CREATIVE);
        helper.assertFalse(mock.canUseGameMasterBlocks(), "the test player is an operator, so the check is not exercised");
        helper.assertFalse(NeonSignItem.mayApplyBlockEntityData(mock, sign), "a non-operator may apply neon sign data from an item");
        helper.assertTrue(NeonSignItem.mayApplyBlockEntityData(mock, new ItemStack(DecorItems.NEON_SIGN.get())), "a neon sign item with no data counts as op-only");

        if (!"Forge".equals(Services.PLATFORM.getPlatformName())) {
            BlockPos floor = new BlockPos(4, 1, 4);
            helper.setBlock(floor, Blocks.STONE);
            ServerPlayer player = helper.makeMockServerPlayerInLevel();
            helper.assertFalse(player.canUseGameMasterBlocks(), "the test player is an operator, so the check is not exercised");
            player.setItemInHand(InteractionHand.MAIN_HAND, sign);
            rightClick(player, helper.getLevel(), sign, helper.absolutePos(floor));

            NeonSignBlockEntity placed = helper.getBlockEntity(floor.above(), NeonSignBlockEntity.class);
            helper.assertTrue(placed.getText(0).getString().isEmpty(), "a non-operator set neon sign text from an item: " + placed.getText(0).getString());
        }
        helper.succeed();
    }
}

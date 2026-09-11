package com.grim3212.assorted.decor.gametest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.MinecraftServer;
import com.grim3212.assorted.lib.platform.Services;
import java.io.BufferedReader;
import java.io.IOException;
import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.api.colorizer.IColorizer;
import com.grim3212.assorted.decor.api.util.DateHandler;
import com.grim3212.assorted.decor.common.blocks.BoneDecorationBlock;
import com.grim3212.assorted.decor.common.blocks.ClayDecorationBlock;
import com.grim3212.assorted.decor.common.blocks.ColorChangingBlock;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.FluroBlock;
import com.grim3212.assorted.decor.common.blocks.IlluminationTubeBlock;
import com.grim3212.assorted.decor.common.blocks.LanternBlock;
import com.grim3212.assorted.decor.common.blocks.PlanterPotBlock;
import com.grim3212.assorted.decor.common.blocks.RoadwayLightBlock;
import com.grim3212.assorted.decor.common.blocks.RoadwayManholeBlock;
import com.grim3212.assorted.decor.common.blocks.RoadwayWhiteBlock;
import com.grim3212.assorted.decor.common.blocks.blockentity.CageBlockEntity;
import com.grim3212.assorted.decor.common.blocks.blockentity.CalendarBlockEntity;
import com.grim3212.assorted.decor.common.blocks.blockentity.ColorizerBlockEntity;
import com.grim3212.assorted.decor.common.blocks.blockentity.NeonSignBlockEntity;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerFireplaceBaseBlock;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerLampPost;
import com.grim3212.assorted.decor.common.entity.DecorEntityTypes;
import com.grim3212.assorted.decor.common.entity.FrameEntity;
import com.grim3212.assorted.decor.common.entity.WallpaperEntity;
import com.grim3212.assorted.decor.common.helpers.DecorCreativeItems;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.util.DyeHelper;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for AssortedDecor.
 * <p>
 * The bodies live in common because the behaviour they check is common; each loader module only
 * registers them into {@code Registries.TEST_FUNCTION} through its own hook, and
 * {@code data/assorteddecor/test_instance/*.json} pairs each one with the shared {@code test_box}
 * structure.
 * <p>
 * This mod is mostly a rendering mod, and a headless gametest sees none of that. What these tests
 * cover is the server side state underneath it: the block a colorizer stores, the colour a paint
 * roller writes into a block state, the light a fireplace emits, the entities frames and wallpapers
 * really are. Anything that needs eyes is in {@code TESTING-CHECKLIST.md}.
 */
public final class DecorGameTests {

    private DecorGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("colorizer_stores_and_reloads", DecorGameTests::colorizerStoresAndReloads);
        out.accept("colorizer_brush_picks_up_and_paints", DecorGameTests::colorizerBrushPicksUpAndPaints);
        out.accept("paint_roller_recolors_blocks", DecorGameTests::paintRollerRecolorsBlocks);
        out.accept("siding_item_keeps_color", DecorGameTests::sidingItemKeepsColor);
        out.accept("cage_holds_mob_and_drops_it", DecorGameTests::cageHoldsMobAndDropsIt);
        out.accept("fireplace_light_follows_active", DecorGameTests::fireplaceLightFollowsActive);
        out.accept("roadway_light_follows_redstone", DecorGameTests::roadwayLightFollowsRedstone);
        out.accept("frame_places_and_drops", DecorGameTests::framePlacesAndDrops);
        out.accept("wallpaper_places_and_drops", DecorGameTests::wallpaperPlacesAndDrops);
        out.accept("colorizer_shapes_take_texture", DecorGameTests::colorizerShapesTakeTexture);
        out.accept("breaking_a_colorizer_does_not_crash", DecorGameTests::breakingAColorizerDoesNotCrash);
        out.accept("fire_colorizers_light_up", DecorGameTests::fireColorizersLightUp);
        out.accept("paint_rollers_recolor_every_color", DecorGameTests::paintRollersRecolorEveryColor);
        out.accept("paint_roller_dyes_a_sheep", DecorGameTests::paintRollerDyesASheep);
        out.accept("fluro_blocks_glow", DecorGameTests::fluroBlocksGlow);
        out.accept("lanterns_place_on_floor_and_ceiling", DecorGameTests::lanternsPlaceOnFloorAndCeiling);
        out.accept("illumination_blocks_emit_light", DecorGameTests::illuminationBlocksEmitLight);
        out.accept("roadway_blocks_place_and_react", DecorGameTests::roadwayBlocksPlaceAndReact);
        out.accept("chain_link_fence_connects", DecorGameTests::chainLinkFenceConnects);
        out.accept("doors_open_on_redstone", DecorGameTests::doorsOpenOnRedstone);
        out.accept("calendar_shows_the_date", DecorGameTests::calendarShowsTheDate);
        out.accept("planter_pot_holds_a_plant", DecorGameTests::planterPotHoldsAPlant);
        out.accept("decoration_blocks_place", DecorGameTests::decorationBlocksPlace);
        out.accept("neon_sign_text_survives_reload", DecorGameTests::neonSignTextSurvivesReload);
        out.accept("mod_assets_are_complete", DecorGameTests::modAssetsAreComplete);
        out.accept("every_recipe_loads_or_is_conditioned_off", DecorGameTests::everyRecipeLoadsOrIsConditionedOff);
    }

    private static final BlockPos MAIN = new BlockPos(4, 1, 4);

    /**
     * A colorizer keeps the block it was given, hands it back, and survives being written out and
     * read again. The stored state moved onto {@code BlockState.CODEC} through
     * {@code ValueInput}/{@code ValueOutput} in the port, which is exactly what the round trip here
     * exercises - a codec that silently wrote nothing would still compile.
     */
    private static void colorizerStoresAndReloads(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(MAIN);
        BlockState stored = Blocks.GOLD_BLOCK.defaultBlockState();

        helper.setBlock(MAIN, DecorBlocks.COLORIZER.get());
        IColorizer colorizer = DecorBlocks.COLORIZER.get();

        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        player.getAbilities().instabuild = true;

        helper.assertTrue(colorizer.setColorizer(level, pos, stored, player, InteractionHand.MAIN_HAND, false),
                "setColorizer refused an empty colorizer");
        helper.assertTrue(colorizer.getStoredState(level, pos) == stored, "colorizer did not report the block it was given");

        ColorizerBlockEntity blockEntity = helper.getBlockEntity(MAIN, ColorizerBlockEntity.class);
        CompoundTag saved = blockEntity.saveWithFullMetadata(level.registryAccess());
        BlockEntity reloaded = BlockEntity.loadStatic(pos, blockEntity.getBlockState(), saved, level.registryAccess());
        helper.assertTrue(reloaded instanceof ColorizerBlockEntity loaded && loaded.getStoredBlockState() == stored,
                "stored block state did not survive a save/load round trip");

        helper.assertTrue(colorizer.clearColorizer(level, pos, player, InteractionHand.MAIN_HAND), "clearColorizer refused a filled colorizer");
        helper.assertTrue(colorizer.getStoredState(level, pos).isAir(), "cleared colorizer still reports a stored block");
        helper.succeed();
    }

    /**
     * The brush end to end, through the real interaction path: right clicking a full cube stores it
     * on the brush, right clicking a colorizer applies it. Driven through
     * {@code ServerPlayerGameMode#useItemOn} rather than {@code GameTestHelper#useBlock} on purpose -
     * the brush hangs off the library's {@code UseBlockEvent}, which is NeoForge's
     * {@code RightClickBlock} on one side and Fabric's {@code UseBlockCallback} on the other, and
     * only a real use fires either.
     */
    private static void colorizerBrushPicksUpAndPaints(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos source = helper.absolutePos(new BlockPos(2, 1, 2));
        BlockPos colorizerPos = helper.absolutePos(MAIN);

        helper.setBlock(new BlockPos(2, 1, 2), Blocks.GOLD_BLOCK);
        helper.setBlock(MAIN, DecorBlocks.COLORIZER.get());

        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.CREATIVE);
        ItemStack brush = new ItemStack(DecorItems.COLORIZER_BRUSH.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, brush);

        rightClick(player, level, brush, source);
        BlockState onBrush = NbtUtils.readBlockState(BuiltInRegistries.BLOCK, NBTHelper.getTag(brush, "stored_state"));
        helper.assertTrue(onBrush.is(Blocks.GOLD_BLOCK), "brush did not pick up the block it was used on");

        rightClick(player, level, brush, colorizerPos);
        helper.assertTrue(DecorBlocks.COLORIZER.get().getStoredState(level, colorizerPos).is(Blocks.GOLD_BLOCK),
                "brush did not apply its stored block to the colorizer");
        helper.succeed();
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
     * A dyed siding item places as that colour. The colour used to be a {@code BlockStateTag} in
     * stack NBT and is {@code DataComponents.BLOCK_STATE} now, which vanilla's {@code BlockItem}
     * applies for us - so this checks the component the mod writes is the one placement reads.
     * <p>
     * Both sidings, in two different colours, so neither the block nor the colour can come out
     * right by accident. They do share {@code ColorChangingItem} and {@code ColorChangingBlock},
     * but only the horizontal one was ever placed here, and "it is the same code path" is exactly
     * the argument that leaves a real gap sitting unnoticed.
     */
    private static void sidingItemKeepsColor(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        placeSidingFromItem(helper, player, new BlockPos(3, 1, 4), DecorBlocks.SIDING_HORIZONTAL.get(), DyeColor.RED);
        placeSidingFromItem(helper, player, new BlockPos(5, 1, 4), DecorBlocks.SIDING_VERTICAL.get(), DyeColor.LIME);

        helper.succeed();
    }

    /** Places one dyed siding on top of {@code floor} and checks it kept both its block and its colour. */
    private static void placeSidingFromItem(GameTestHelper helper, Player player, BlockPos floor, Block siding, DyeColor color) {
        BlockPos placed = floor.above();
        helper.setBlock(floor, Blocks.STONE);

        ItemStack stack = ColorChangingBlock.getColorStack(new ItemStack(siding), color);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(floor))));

        helper.assertBlockPresent(siding, placed);
        helper.assertBlockProperty(placed, ColorChangingBlock.COLOR, color);
    }

    /**
     * A cage builds its display mob from the caged stack, reports it to a comparator, and gives the
     * stack back when broken. The drop moved to {@code CageBlockEntity#preRemoveSideEffects} during
     * the port because the block entity is gone by the time the block's removal hook runs.
     */
    private static void cageHoldsMobAndDropsIt(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(MAIN);

        helper.setBlock(MAIN, DecorBlocks.CAGE.get());
        CageBlockEntity cage = helper.getBlockEntity(MAIN, CageBlockEntity.class);

        ItemStack egg = new ItemStack(Items.PIG_SPAWN_EGG);
        helper.assertTrue(CageBlockEntity.isValidCage(egg) != null, "cage rejected a spawn egg");
        cage.getItemStackStorageHandler().setStackInSlot(0, egg);

        Entity caged = cage.getCachedEntity();
        helper.assertTrue(caged != null && caged.getType() == EntityTypes.PIG, "cage did not build a pig out of the spawn egg");
        helper.assertTrue(level.getBlockState(pos).getAnalogOutputSignal(level, pos, Direction.UP) > 0,
                "a stocked cage gave a comparator nothing to read");

        helper.destroyBlock(MAIN);
        helper.succeedWhen(() -> helper.assertItemEntityPresent(Items.PIG_SPAWN_EGG, MAIN, 3.0D));
    }

    /**
     * Lighting a fireplace makes it emit light, and putting it out stops it. The light did not come
     * on at all until the block was re-textured during the port, so this is the regression that
     * bug leaves behind: it needs {@code getLightEmission(state, level, pos)} to reach the light
     * engine, which is a NeoForge block extension on one loader and a library mixin on the other.
     */
    private static void fireplaceLightFollowsActive(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(MAIN);

        helper.setBlock(MAIN, DecorBlocks.COLORIZER_FIREPLACE.get());
        helper.assertValueEqual(lightEmission(helper, MAIN), 0, "an unlit fireplace claimed to emit light");

        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        ItemStack flintAndSteel = new ItemStack(Items.FLINT_AND_STEEL);
        player.setItemInHand(InteractionHand.MAIN_HAND, flintAndSteel);
        helper.runBeforeTestEnd(() -> helper.setBlock(MAIN, Blocks.AIR));

        helper.startSequence()
                .thenExecute(() -> rightClick(player, level, flintAndSteel, pos))
                .thenExecute(() -> helper.assertBlockProperty(MAIN, ColorizerFireplaceBaseBlock.ACTIVE, true))
                .thenExecute(() -> helper.assertValueEqual(lightEmission(helper, MAIN), 15, "a lit fireplace did not claim to emit light"))
                // The one place in this suite that still asks the light engine, because the bug this
                // test guards was the emission never reaching it - every assertion around this one
                // would be satisfied by a getLightEmission the engine never queries. Written as "at
                // least what the block declares" rather than "exactly 15" so a neighbouring test
                // cannot break it: foreign light only ever pushes this number up, so the bound never
                // fails spuriously, and the 5-and-6 box spacing keeps any foreign contribution well
                // under 15, so it does not pass spuriously either.
                .thenWaitUntil(() -> helper.assertTrue(level.getBrightness(LightLayer.BLOCK, pos) >= lightEmission(helper, MAIN),
                        "a lit fireplace's light emission never reached the light engine"))
                .thenExecute(() -> level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(ColorizerFireplaceBaseBlock.ACTIVE, false)))
                .thenExecute(() -> helper.assertValueEqual(lightEmission(helper, MAIN), 0, "an extinguished fireplace kept claiming to emit light"))
                .thenSucceed();
    }

    /**
     * The roadway light turns on the moment it is powered and off four ticks after the power goes
     * away, and its light level follows the state both ways.
     */
    private static void roadwayLightFollowsRedstone(GameTestHelper helper) {
        BlockPos lamp = new BlockPos(4, 1, 4);
        BlockPos power = new BlockPos(5, 1, 4);

        helper.setBlock(lamp, DecorBlocks.ROADWAY_LIGHT.get());
        helper.runBeforeTestEnd(() -> helper.setBlock(lamp, Blocks.AIR));

        helper.startSequence()
                .thenExecute(() -> helper.setBlock(power, Blocks.REDSTONE_BLOCK))
                .thenExecute(() -> helper.assertBlockProperty(lamp, RoadwayLightBlock.ACTIVE, true))
                .thenExecute(() -> helper.assertValueEqual(lightEmission(helper, lamp), 15, "a powered roadway light did not claim to emit light"))
                .thenExecute(() -> helper.setBlock(power, Blocks.AIR))
                .thenWaitUntil(() -> helper.assertBlockProperty(lamp, RoadwayLightBlock.ACTIVE, false))
                .thenExecute(() -> helper.assertValueEqual(lightEmission(helper, lamp), 0, "an unpowered roadway light kept claiming to emit light"))
                .thenSucceed();
    }

    /**
     * A frame placed from its item becomes a real entity on the wall and gives the item back when
     * it is broken. Frames are {@code BlockAttachedEntity} subclasses, whose bounding box and
     * survival checks were restructured in the port.
     */
    private static void framePlacesAndDrops(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos wall = new BlockPos(4, 2, 4);
        BlockPos hanging = wall.south();

        helper.setBlock(wall, Blocks.STONE);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack frameItem = new ItemStack(DecorItems.WOOD_FRAME.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, frameItem);
        frameItem.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitSide(helper.absolutePos(wall), Direction.SOUTH)));

        helper.assertEntityPresent(DecorEntityTypes.WOOD_FRAME.get(), hanging);

        FrameEntity frame = helper.getEntities(DecorEntityTypes.WOOD_FRAME.get()).getFirst();
        helper.assertTrue(frame.survives(), "a frame on a stone wall did not think it could survive there");
        helper.hurt(frame, level.damageSources().generic(), 100.0F);

        helper.succeedWhen(() -> helper.assertItemEntityPresent(DecorItems.WOOD_FRAME.get(), hanging, 2.0D));
    }

    /**
     * The same for wallpaper, which overrides {@code survives} so several can share one wall, and
     * whose entity carries its pattern and dye in synched data.
     */
    private static void wallpaperPlacesAndDrops(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos wall = new BlockPos(4, 2, 4);
        BlockPos hanging = wall.south();

        helper.setBlock(wall, Blocks.STONE);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack wallpaperItem = new ItemStack(DecorItems.WALLPAPER.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, wallpaperItem);
        wallpaperItem.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitSide(helper.absolutePos(wall), Direction.SOUTH)));

        helper.assertEntityPresent(DecorEntityTypes.WALLPAPER.get(), hanging);

        WallpaperEntity wallpaper = helper.getEntities(DecorEntityTypes.WALLPAPER.get()).getFirst();
        wallpaper.dyeWallpaper(DyeColor.RED);
        helper.assertValueEqual(wallpaper.getWallpaperColor()[0], (DyeColor.RED.getFireworkColor() & 0xFF0000) >> 16, "wallpaper did not take the dye");

        helper.hurt(wallpaper, level.damageSources().generic(), 100.0F);
        helper.succeedWhen(() -> helper.assertItemEntityPresent(DecorItems.WALLPAPER.get(), hanging, 2.0D));
    }

    /**
     * Every colorizer shape stores a block, hands it back and lets go of it again.
     * <p>
     * Driven off {@link DecorBlocks#colorizerBlocks()} rather than a list written out here, so a
     * shape added later is covered the day it is registered. The multi block shapes are the
     * interesting ones: the door and the lamp post override {@code setColorizer} to carry the block
     * across every part of themselves, and refuse unless all of those parts are really there.
     * <p>
     * The result of {@code clearColorizer} is deliberately not asserted. Those same two shapes
     * always return false from it - their outer call clears the other parts through the overridden
     * {@code setColorizer} and then finds them already empty - so what is checked is the thing that
     * matters and is actually true, that the colorizer ends up holding nothing. Nothing in
     * {@code main} reads that return value.
     */
    private static void colorizerShapesTakeTexture(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(MAIN);
        BlockState stored = Blocks.GOLD_BLOCK.defaultBlockState();

        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        player.getAbilities().instabuild = true;

        for (IRegistryObject<? extends Block> registered : DecorBlocks.colorizerBlocks()) {
            Block block = registered.get();
            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();

            helper.assertTrue(block instanceof IColorizer, name + " is in colorizerBlocks() but is not an IColorizer");
            IColorizer colorizer = (IColorizer) block;

            placeShape(helper, MAIN, block.defaultBlockState());

            helper.assertTrue(colorizer.setColorizer(level, pos, stored, player, InteractionHand.MAIN_HAND, false), name + " refused a block");
            helper.assertTrue(colorizer.getStoredState(level, pos).is(Blocks.GOLD_BLOCK), name + " did not hand back the block it was given");

            colorizer.clearColorizer(level, pos, player, InteractionHand.MAIN_HAND);
            helper.assertTrue(colorizer.getStoredState(level, pos).isAir(), name + " still reports a stored block after being cleared");

            for (int y = 3; y >= 1; y--) {
                helper.setBlock(new BlockPos(MAIN.getX(), y, MAIN.getZ()), Blocks.AIR);
            }
        }

        helper.succeed();
    }

    /**
     * Breaking a filled colorizer leaves air behind instead of throwing. Everything the block
     * overrides - sound type, friction, landing and running effects - reads the stored state out of
     * the block entity by position, and the break path asks for some of that after the block entity
     * has already gone. {@code ServerLevel.destroyBlock} rather than
     * {@code GameTestHelper#destroyBlock}, which passes {@code dropBlock = false} and skips it all.
     */
    private static void breakingAColorizerDoesNotCrash(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(MAIN);

        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        player.getAbilities().instabuild = true;

        for (Block block : List.of(DecorBlocks.COLORIZER.get(), DecorBlocks.COLORIZER_STAIRS.get(), DecorBlocks.COLORIZER_SLAB.get(), DecorBlocks.COLORIZER_FIREPLACE.get())) {
            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();

            helper.setBlock(MAIN, block);
            ((IColorizer) block).setColorizer(level, pos, Blocks.GOLD_BLOCK.defaultBlockState(), player, InteractionHand.MAIN_HAND, false);

            helper.assertTrue(level.destroyBlock(pos, true), "breaking a filled " + name + " was refused");
            helper.assertBlockPresent(Blocks.AIR, MAIN);
        }

        helper.succeed();
    }

    /**
     * The firepit, the covered firepit, the firering and the stove all light from flint and steel
     * and reach the light engine. The fireplace has its own test above; these four share
     * {@link ColorizerFireplaceBaseBlock} with it but are separate registrations, and it was only
     * the fireplace that got checked when the light emission was fixed during the port.
     * <p>
     * Each is put out again before the test ends. Nothing asserts on ambient brightness any more -
     * see {@link #lightEmission} - but a block left burning is still noise in a shared world, and
     * the fireplace test next door does look at the light engine once.
     */
    private static void fireColorizersLightUp(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();

        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        ItemStack flintAndSteel = new ItemStack(Items.FLINT_AND_STEEL);
        player.setItemInHand(InteractionHand.MAIN_HAND, flintAndSteel);

        List<Block> fires = List.of(DecorBlocks.COLORIZER_FIREPIT.get(), DecorBlocks.COLORIZER_FIREPIT_COVERED.get(), DecorBlocks.COLORIZER_FIRERING.get(), DecorBlocks.COLORIZER_STOVE.get());
        List<BlockPos> at = new ArrayList<>();

        for (int i = 0; i < fires.size(); i++) {
            BlockPos rel = new BlockPos(3 + i, 1, 4);
            helper.setBlock(rel, fires.get(i));
            helper.assertBlockProperty(rel, ColorizerFireplaceBaseBlock.ACTIVE, false);
            helper.assertValueEqual(lightEmission(helper, rel), 0,
                    "an unlit " + BuiltInRegistries.BLOCK.getKey(fires.get(i)).getPath() + " claimed to emit light");
            at.add(rel);
        }
        helper.runBeforeTestEnd(() -> at.forEach(rel -> helper.setBlock(rel, Blocks.AIR)));

        helper.startSequence()
                .thenExecute(() -> at.forEach(rel -> rightClick(player, level, flintAndSteel, helper.absolutePos(rel))))
                .thenExecute(() -> at.forEach(rel -> {
                    helper.assertBlockProperty(rel, ColorizerFireplaceBaseBlock.ACTIVE, true);
                    helper.assertValueEqual(lightEmission(helper, rel), 15,
                            "a lit " + BuiltInRegistries.BLOCK.getKey(level.getBlockState(helper.absolutePos(rel)).getBlock()).getPath() + " did not claim to emit light");
                }))
                .thenSucceed();
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

    /** All sixteen fluro blocks declare full block light. */
    private static void fluroBlocksGlow(GameTestHelper helper) {
        Map<DyeColor, BlockPos> at = new java.util.EnumMap<>(DyeColor.class);

        int i = 0;
        for (DyeColor color : DyeColor.values()) {
            BlockPos rel = new BlockPos(3 + i % 4, 1 + i / 4, 4);
            helper.setBlock(rel, FluroBlock.FLURO_BY_DYE.get(color).get());
            at.put(color, rel);
            i++;
        }
        helper.runBeforeTestEnd(() -> at.values().forEach(rel -> helper.setBlock(rel, Blocks.AIR)));

        at.forEach((color, rel) -> helper.assertValueEqual(lightEmission(helper, rel), 15,
                "the " + color.getName() + " fluro block did not claim to emit light"));
        helper.succeed();
    }

    /**
     * Each lantern places from its item onto a floor and onto a ceiling. They carry no attachment
     * property - the model is the same either way - so what is worth asserting is that both
     * placements land a real, unwaterlogged lantern that still emits its light.
     */
    private static void lanternsPlaceOnFloorAndCeiling(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos floor = new BlockPos(4, 1, 2);
        BlockPos ceiling = new BlockPos(4, 5, 2);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        for (Block lantern : List.of(DecorBlocks.PAPER_LANTERN.get(), DecorBlocks.BONE_LANTERN.get(), DecorBlocks.IRON_LANTERN.get())) {
            String name = BuiltInRegistries.BLOCK.getKey(lantern).getPath();
            helper.setBlock(floor, Blocks.STONE);
            helper.setBlock(ceiling, Blocks.STONE);

            ItemStack stack = new ItemStack(lantern, 2);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitSide(helper.absolutePos(floor), Direction.UP)));
            stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitSide(helper.absolutePos(ceiling), Direction.DOWN)));

            for (BlockPos placed : List.of(floor.above(), ceiling.below())) {
                BlockPos pos = helper.absolutePos(placed);
                helper.assertBlockPresent(lantern, placed);
                helper.assertBlockProperty(placed, LanternBlock.WATERLOGGED, false);
                helper.assertTrue(level.getBlockState(pos).canSurvive(level, pos), "a placed " + name + " did not think it could survive where it landed");
                helper.assertValueEqual(lightEmission(helper, placed), 14, name + " light emission");
                helper.setBlock(placed, Blocks.AIR);
            }
        }

        helper.succeed();
    }

    /** The illumination tube and plate both declare full block light standing on the floor. */
    private static void illuminationBlocksEmitLight(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos tube = new BlockPos(3, 1, 4);
        BlockPos plate = new BlockPos(5, 1, 4);

        helper.setBlock(tube, DecorBlocks.ILLUMINATION_TUBE.get().defaultBlockState().setValue(IlluminationTubeBlock.FACING, Direction.UP));
        helper.setBlock(plate, DecorBlocks.ILLUMINATION_PLATE.get().defaultBlockState().setValue(IlluminationTubeBlock.FACING, Direction.UP));

        helper.runBeforeTestEnd(() -> {
            helper.setBlock(tube, Blocks.AIR);
            helper.setBlock(plate, Blocks.AIR);
        });

        for (BlockPos rel : List.of(tube, plate)) {
            BlockPos pos = helper.absolutePos(rel);
            helper.assertTrue(level.getBlockState(pos).canSurvive(level, pos), "an illumination block standing on the floor did not think it could survive there");
        }

        helper.assertValueEqual(lightEmission(helper, tube), 15, "illumination tube light emission");
        helper.assertValueEqual(lightEmission(helper, plate), 15, "illumination plate light emission");
        helper.succeed();
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
     * The creative tab is registered, and every block and item this mod adds has a model and a
     * name. Missing models and missing lang keys were the single most repeated bug of the port, and
     * a headless server can see both: the mod's own assets are on its classpath even though it
     * never loads them.
     * <p>
     * Everything missing is reported in one message rather than failing on the first, so fixing
     * them is one pass and not a loop.
     */
    private static void modAssetsAreComplete(GameTestHelper helper) {
        helper.assertTrue(BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(DecorCreativeItems.CREATIVE_TAB_KEY),
                "the Assorted Decor creative tab is not registered");

        JsonObject lang = readJson("/assets/" + Constants.MOD_ID + "/lang/en_us.json");
        helper.assertTrue(lang != null, "/assets/" + Constants.MOD_ID + "/lang/en_us.json is not on the classpath");
        helper.assertTrue(lang.has("itemGroup." + Constants.MOD_ID), "the creative tab has no name in en_us.json");

        List<String> missing = new ArrayList<>();

        for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
            Identifier id = entry.getKey().identifier();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            if (!resourceExists("/assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json")) {
                missing.add("blockstate for " + id);
            }
            if (!hasName(lang, entry.getValue().getDescriptionId())) {
                missing.add("name " + entry.getValue().getDescriptionId() + " for block " + id);
            }
        }

        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            Identifier id = entry.getKey().identifier();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            if (!resourceExists("/assets/" + id.getNamespace() + "/items/" + id.getPath() + ".json")) {
                missing.add("item model for " + id);
            }
            if (!hasName(lang, entry.getValue().getDescriptionId())) {
                missing.add("name " + entry.getValue().getDescriptionId() + " for item " + id);
            }
        }

        helper.assertTrue(missing.isEmpty(), missing.size() + " missing assets: " + String.join("; ", missing));
        helper.succeed();
    }

    /**
     * Whether {@code key} names something in the language file. A suffixed key counts: a colour
     * changing item like siding overrides {@code getName} to translate
     * {@code <descriptionId>_<colour>}, so its bare description id is never meant to be there.
     */
    private static boolean hasName(JsonObject lang, String key) {
        if (lang.has(key)) {
            return true;
        }
        String prefix = key + "_";
        for (Map.Entry<String, JsonElement> entry : lang.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private static JsonObject readJson(String path) {
        try (InputStream in = DecorGameTests.class.getResourceAsStream(path)) {
            return in == null ? null : JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean resourceExists(String path) {
        try (InputStream in = DecorGameTests.class.getResourceAsStream(path)) {
            return in != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Puts a colorizer down ready to be filled. Three shapes need more than a bare
     * {@code setBlock}: a door only accepts a stored block when both of its halves are there, a
     * lamp post only when all three of its parts are, and a side attached shape ({@code table},
     * {@code counter}, {@code stool}, the pyramids) is placed against a wall by default and has
     * nothing to hold onto in mid air.
     */
    private static void placeShape(GameTestHelper helper, BlockPos rel, BlockState state) {
        if (state.hasProperty(BlockStateProperties.ATTACH_FACE)) {
            state = state.setValue(BlockStateProperties.ATTACH_FACE, AttachFace.FLOOR);
        }

        if (state.hasProperty(DoorBlock.HALF)) {
            helper.setBlock(rel, state.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
            helper.setBlock(rel.above(), state.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        } else if (state.hasProperty(ColorizerLampPost.PART)) {
            helper.setBlock(rel, state.setValue(ColorizerLampPost.PART, ColorizerLampPost.LampPart.BOTTOM));
            helper.setBlock(rel.above(), state.setValue(ColorizerLampPost.PART, ColorizerLampPost.LampPart.MIDDLE));
            helper.setBlock(rel.above(2), state.setValue(ColorizerLampPost.PART, ColorizerLampPost.LampPart.TOP));
        } else {
            helper.setBlock(rel, state);
        }
    }

    /**
     * The light level the block at {@code rel} declares for itself, in the state and position it is
     * actually in.
     * <p>
     * This is what every light assertion in this file asks, rather than
     * {@code level.getBrightness(LightLayer.BLOCK, pos)}. Brightness is the light engine's view of
     * the whole world at a position, and any light source in a neighbouring test box raises it.
     * Tests in a batch run at the same time, so a neighbour is lit at the moment this one measures;
     * {@code StructureGridSpawner} spaces boxes a fixed 5 and 6 apart with no way to ask for more,
     * and block light of 15 carries much further than that. An equality on brightness is therefore
     * only ever true by luck of the layout, which is what made {@code roadway_light_follows_redstone}
     * fail on one loader and pass on the next run with the same code.
     * <p>
     * {@code BlockGetter#getLightEmission} asks the block instead: NeoForge patches it, and the
     * library mixes into it on Fabric, so on both loaders an {@code IBlockLightEmission} block - the
     * colorizers - answers for its own state and position, and a plain {@code lightLevel} block
     * answers from its properties. Nothing outside the block can change the answer.
     */
    private static int lightEmission(GameTestHelper helper, BlockPos rel) {
        return helper.getLevel().getLightEmission(helper.absolutePos(rel));
    }

    /** A right click on the top face of {@code pos}, through the path that fires the loader's use-block event. */
    private static void rightClick(ServerPlayer player, ServerLevel level, ItemStack stack, BlockPos pos) {
        player.gameMode.useItemOn(player, level, stack, InteractionHand.MAIN_HAND, hitTop(pos));
    }

    private static BlockHitResult hitTop(BlockPos pos) {
        return hitSide(pos, Direction.UP);
    }

    private static BlockHitResult hitSide(BlockPos pos, Direction face) {
        return new BlockHitResult(Vec3.atCenterOf(pos).relative(face, 0.5D), face, pos, false);
    }
    /**
     * Every recipe file this mod ships either loaded, or carries this loader's load conditions and was
     * skipped by them. A file with neither failed to parse. On Fabric that was every conditional
     * recipe for a while: Fabric's datagen wrote them without conditions, and the NeoForge copy that
     * shadowed it carries a key Fabric ignores - so only this loader's own key counts.
     */
    private static void everyRecipeLoadsOrIsConditionedOff(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        FileToIdConverter recipes = FileToIdConverter.json("recipe");
        String conditionsKey = Services.PLATFORM.getPlatformName().equals("Fabric") ? "fabric:load_conditions" : "neoforge:conditions";
        List<String> failed = new ArrayList<>();

        recipes.listMatchingResources(server.getResourceManager()).forEach((file, resource) -> {
            Identifier id = recipes.fileToId(file);
            if (!id.getNamespace().equals(Constants.MOD_ID) || server.getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, id)).isPresent()) {
                return;
            }

            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                if (!json.has(conditionsKey)) {
                    failed.add(id.toString());
                }
            } catch (IOException e) {
                failed.add(id + " (" + e.getMessage() + ")");
            }
        });

        helper.assertTrue(failed.isEmpty(), failed.size() + " recipes failed to load without being conditioned off: " + String.join(", ", failed.subList(0, Math.min(10, failed.size()))));
        helper.succeed();
    }
}

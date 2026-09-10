package com.grim3212.assorted.decor.gametest;

import com.grim3212.assorted.decor.api.colorizer.IColorizer;
import com.grim3212.assorted.decor.common.blocks.ColorChangingBlock;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.RoadwayLightBlock;
import com.grim3212.assorted.decor.common.blocks.blockentity.CageBlockEntity;
import com.grim3212.assorted.decor.common.blocks.blockentity.ColorizerBlockEntity;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerFireplaceBaseBlock;
import com.grim3212.assorted.decor.common.entity.DecorEntityTypes;
import com.grim3212.assorted.decor.common.entity.FrameEntity;
import com.grim3212.assorted.decor.common.entity.WallpaperEntity;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.lib.util.DyeHelper;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

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
     */
    private static void sidingItemKeepsColor(GameTestHelper helper) {
        BlockPos floor = new BlockPos(4, 1, 4);
        BlockPos placed = floor.above();

        helper.setBlock(floor, Blocks.STONE);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack stack = ColorChangingBlock.getColorStack(new ItemStack(DecorBlocks.SIDING_HORIZONTAL.get()), DyeColor.RED);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);

        stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(floor))));

        helper.assertBlockPresent(DecorBlocks.SIDING_HORIZONTAL.get(), placed);
        helper.assertBlockProperty(placed, ColorChangingBlock.COLOR, DyeColor.RED);
        helper.succeed();
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
        helper.assertValueEqual(level.getBrightness(LightLayer.BLOCK, pos), 0, "an unlit fireplace was emitting light");

        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        ItemStack flintAndSteel = new ItemStack(Items.FLINT_AND_STEEL);
        player.setItemInHand(InteractionHand.MAIN_HAND, flintAndSteel);

        helper.startSequence()
                .thenExecute(() -> rightClick(player, level, flintAndSteel, pos))
                .thenExecute(() -> helper.assertBlockProperty(MAIN, ColorizerFireplaceBaseBlock.ACTIVE, true))
                .thenWaitUntil(() -> helper.assertValueEqual(level.getBrightness(LightLayer.BLOCK, pos), 15, "a lit fireplace emitted no light"))
                .thenExecute(() -> level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(ColorizerFireplaceBaseBlock.ACTIVE, false)))
                .thenWaitUntil(() -> helper.assertValueEqual(level.getBrightness(LightLayer.BLOCK, pos), 0, "an extinguished fireplace kept emitting light"))
                .thenSucceed();
    }

    /**
     * The roadway light turns on the moment it is powered and off four ticks after the power goes
     * away, and its light level follows the state both ways.
     */
    private static void roadwayLightFollowsRedstone(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos lamp = new BlockPos(4, 1, 4);
        BlockPos power = new BlockPos(5, 1, 4);
        BlockPos lampPos = helper.absolutePos(lamp);

        helper.setBlock(lamp, DecorBlocks.ROADWAY_LIGHT.get());

        helper.startSequence()
                .thenExecute(() -> helper.setBlock(power, Blocks.REDSTONE_BLOCK))
                .thenExecute(() -> helper.assertBlockProperty(lamp, RoadwayLightBlock.ACTIVE, true))
                .thenWaitUntil(() -> helper.assertValueEqual(level.getBrightness(LightLayer.BLOCK, lampPos), 15, "a powered roadway light emitted no light"))
                .thenExecute(() -> helper.setBlock(power, Blocks.AIR))
                .thenWaitUntil(() -> helper.assertBlockProperty(lamp, RoadwayLightBlock.ACTIVE, false))
                .thenWaitUntil(() -> helper.assertValueEqual(level.getBrightness(LightLayer.BLOCK, lampPos), 0, "an unpowered roadway light kept emitting light"))
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
}

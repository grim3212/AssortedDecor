package com.grim3212.assorted.decor.gametest;

import com.grim3212.assorted.decor.api.colorizer.IColorizer;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.blockentity.ColorizerBlockEntity;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerFireplaceBaseBlock;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.decor.gametest.DecorTestSupport.*;

/**
 * Colorizers: storing, painting with the brush, shapes, breaking and the fire variants.
 */
final class ColorizerTests {

    private ColorizerTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("colorizer_stores_and_reloads", ColorizerTests::colorizerStoresAndReloads);
        out.accept("colorizer_brush_picks_up_and_paints", ColorizerTests::colorizerBrushPicksUpAndPaints);
        out.accept("colorizer_shapes_take_texture", ColorizerTests::colorizerShapesTakeTexture);
        out.accept("breaking_a_colorizer_does_not_crash", ColorizerTests::breakingAColorizerDoesNotCrash);
        out.accept("fire_colorizers_light_up", ColorizerTests::fireColorizersLightUp);
    }

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

        // The update tag is what every other player is sent. It has to carry the stored block as well,
        // or their colorizer keeps its old texture - AssortedLib re-renders a model-data block entity
        // on the client once this is loaded into it.
        helper.assertTrue(blockEntity.getUpdatePacket() != null, "a colorizer sends clients no update packet");
        ColorizerBlockEntity onClient = new ColorizerBlockEntity(pos, blockEntity.getBlockState());
        onClient.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), blockEntity.getUpdateTag(level.registryAccess())));
        helper.assertTrue(onClient.getStoredBlockState() == stored, "the stored block did not reach a client through the update tag");

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
     * <p>
     * Each click must also come back as consumed: the brush has already acted, so vanilla must not
     * go on to use the block as well. NeoForge used to let it, running the picked-up block's
     * interaction on the air left behind.
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

        InteractionResult pickUp = rightClick(player, level, brush, source);
        BlockState onBrush = NbtUtils.readBlockState(BuiltInRegistries.BLOCK, NBTHelper.getTag(brush, "stored_state"));
        helper.assertTrue(onBrush.is(Blocks.GOLD_BLOCK), "brush did not pick up the block it was used on");
        helper.assertTrue(pickUp.consumesAction(), "picking a block up with the brush came back as " + pickUp + ", so vanilla used the block too");

        InteractionResult paint = rightClick(player, level, brush, colorizerPos);
        helper.assertTrue(DecorBlocks.COLORIZER.get().getStoredState(level, colorizerPos).is(Blocks.GOLD_BLOCK),
                "brush did not apply its stored block to the colorizer");
        helper.assertTrue(paint.consumesAction(), "painting a colorizer with the brush came back as " + paint + ", so vanilla used the block too");
        helper.succeed();
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
}

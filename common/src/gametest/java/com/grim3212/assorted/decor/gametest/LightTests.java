package com.grim3212.assorted.decor.gametest;

import com.grim3212.assorted.decor.api.colorizer.IColorizer;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.FluroBlock;
import com.grim3212.assorted.decor.common.blocks.IlluminationTubeBlock;
import com.grim3212.assorted.decor.common.blocks.LanternBlock;
import com.grim3212.assorted.decor.common.blocks.RoadwayLightBlock;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerFireplaceBaseBlock;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.decor.gametest.DecorTestSupport.*;

/**
 * Blocks that give off light: fireplaces, roadway lights, fluro blocks, lanterns and illumination blocks.
 */
final class LightTests {

    private LightTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("fireplace_light_follows_active", LightTests::fireplaceLightFollowsActive);
        out.accept("roadway_light_follows_redstone", LightTests::roadwayLightFollowsRedstone);
        out.accept("fluro_blocks_glow", LightTests::fluroBlocksGlow);
        out.accept("lanterns_place_on_floor_and_ceiling", LightTests::lanternsPlaceOnFloorAndCeiling);
        out.accept("illumination_blocks_emit_light", LightTests::illuminationBlocksEmitLight);
        out.accept("colorizer_takes_its_blocks_light_dampening", LightTests::colorizerTakesItsBlocksLightDampening);
        out.accept("colorizer_blocks_light_like_its_block", LightTests::colorizerBlocksLightLikeItsBlock);
        out.accept("colorizer_blocks_skylight_like_its_block", LightTests::colorizerBlocksSkylightLikeItsBlock);
        out.accept("colorizer_emits_light_like_its_block", LightTests::colorizerEmitsLightLikeItsBlock);
    }

    /**
     * A glowstone filled colorizer glows in the server's light engine, not only the client's. The
     * engine asks the block from its own thread with the level in hand, where {@code Level#getBlockEntity}
     * answers null, which is what {@code IColorizer#getStoredState}'s chunk read is for. A stairs is
     * checked beside the cube, since every shape emits what it stores. Asserted at the colorizer's
     * own position: an emitter reads 15 there, and no other test's light can be 15 that far away.
     */
    private static void colorizerEmitsLightLikeItsBlock(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos cubeRel = new BlockPos(2, 1, 4);
        BlockPos stairsRel = new BlockPos(6, 1, 4);
        BlockPos cubePos = helper.absolutePos(cubeRel);
        BlockPos stairsPos = helper.absolutePos(stairsRel);

        helper.setBlock(cubeRel, DecorBlocks.COLORIZER.get());
        helper.setBlock(stairsRel, DecorBlocks.COLORIZER_STAIRS.get());
        helper.runBeforeTestEnd(() -> {
            helper.setBlock(cubeRel, Blocks.AIR);
            helper.setBlock(stairsRel, Blocks.AIR);
        });

        IColorizer cube = DecorBlocks.COLORIZER.get();
        IColorizer stairs = DecorBlocks.COLORIZER_STAIRS.get();
        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        player.getAbilities().instabuild = true;
        BlockState glowstone = Blocks.GLOWSTONE.defaultBlockState();

        helper.startSequence()
                .thenExecute(() -> {
                    helper.assertTrue(cube.setColorizer(level, cubePos, glowstone, player, InteractionHand.MAIN_HAND, false), "setColorizer refused the colorizer");
                    helper.assertTrue(stairs.setColorizer(level, stairsPos, glowstone, player, InteractionHand.MAIN_HAND, false), "setColorizer refused the colorizer stairs");
                })
                .thenWaitUntil(() -> {
                    helper.assertValueEqual(level.getBrightness(LightLayer.BLOCK, cubePos), 15, "the server's block light at a glowstone filled colorizer");
                    helper.assertValueEqual(level.getBrightness(LightLayer.BLOCK, stairsPos), 15, "the server's block light at glowstone filled colorizer stairs");
                })
                .thenExecute(() -> {
                    helper.assertTrue(cube.clearColorizer(level, cubePos, player, InteractionHand.MAIN_HAND), "clearColorizer refused the colorizer");
                    helper.assertTrue(stairs.clearColorizer(level, stairsPos, player, InteractionHand.MAIN_HAND), "clearColorizer refused the colorizer stairs");
                })
                .thenWaitUntil(() -> {
                    helper.assertTrue(level.getBrightness(LightLayer.BLOCK, cubePos) < 15, "a cleared colorizer still reads as a light source on the server");
                    helper.assertTrue(level.getBrightness(LightLayer.BLOCK, stairsPos) < 15, "cleared colorizer stairs still read as a light source on the server");
                })
                .thenSucceed();
    }

    /**
     * The same statement for sky light, which is a separate engine with its own copy of the opacity
     * question - block light passing is no evidence that sky light does.
     * <p>
     * The probe is walled in on all four sides with the colorizer as its roof and open sky above
     * that, because sky light spreads sideways as well as down: without the walls it would arrive
     * from the neighbouring columns and the test would pass whatever the roof did.
     */
    private static void colorizerBlocksSkylightLikeItsBlock(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos probe = new BlockPos(2, 1, 6);
        BlockPos colorizerRel = probe.above();
        List<BlockPos> walls = List.of(probe.north(), probe.south(), probe.east(), probe.west());

        walls.forEach(wall -> helper.setBlock(wall, Blocks.STONE));
        helper.setBlock(colorizerRel, DecorBlocks.COLORIZER.get());
        helper.runBeforeTestEnd(() -> {
            walls.forEach(wall -> helper.setBlock(wall, Blocks.AIR));
            helper.setBlock(colorizerRel, Blocks.AIR);
        });

        BlockPos probePos = helper.absolutePos(probe);
        BlockPos colorizerPos = helper.absolutePos(colorizerRel);
        IColorizer colorizer = DecorBlocks.COLORIZER.get();
        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        player.getAbilities().instabuild = true;

        helper.startSequence()
                // An empty colorizer is a whole block and stops light itself, so glass is what lets
                // the sky in. Says the roofed probe really does see sky through it; without this the
                // assertion below would also hold in a box that never had sky access at all.
                .thenExecute(() -> helper.assertTrue(
                        colorizer.setColorizer(level, colorizerPos, Blocks.GLASS.defaultBlockState(), player, InteractionHand.MAIN_HAND, false),
                        "setColorizer refused an empty colorizer"))
                .thenWaitUntil(() -> helper.assertTrue(level.getBrightness(LightLayer.SKY, probePos) > 0,
                        "no sky light reached under a glass filled colorizer, so this test would prove nothing"))
                .thenExecute(() -> helper.assertTrue(
                        colorizer.setColorizer(level, colorizerPos, Blocks.STONE.defaultBlockState(), player, InteractionHand.MAIN_HAND, false),
                        "setColorizer refused a filled colorizer"))
                .thenWaitUntil(() -> helper.assertValueEqual(level.getBrightness(LightLayer.SKY, probePos), 0,
                        "the sky light a stone filled colorizer let through to the probe beneath it"))
                .thenSucceed();
    }

    /**
     * A filled colorizer stops block light the way its stored block would, asserted through the light
     * engine itself rather than by asking the block.
     * <p>
     * The probe is sealed in stone on five sides, with the colorizer as the sixth and a glowstone
     * block beyond it, so the only path light can take is through the colorizer - which also makes
     * the assertion immune to the light a concurrently running neighbouring test box bleeds in. A
     * glass filled colorizer dampens by one, so light arrives; a stone filled one takes all fifteen,
     * so none does. (An empty one is a whole block and stops it too.)
     */
    private static void colorizerBlocksLightLikeItsBlock(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos probe = new BlockPos(4, 2, 4);
        BlockPos colorizerRel = probe.east();
        BlockPos glowstoneRel = probe.east(2);
        List<BlockPos> walls = List.of(probe.below(), probe.above(), probe.west(), probe.north(), probe.south());

        walls.forEach(wall -> helper.setBlock(wall, Blocks.STONE));
        helper.setBlock(colorizerRel, DecorBlocks.COLORIZER.get());
        helper.setBlock(glowstoneRel, Blocks.GLOWSTONE);
        helper.runBeforeTestEnd(() -> {
            walls.forEach(wall -> helper.setBlock(wall, Blocks.AIR));
            helper.setBlock(colorizerRel, Blocks.AIR);
            helper.setBlock(glowstoneRel, Blocks.AIR);
        });

        BlockPos probePos = helper.absolutePos(probe);
        BlockPos colorizerPos = helper.absolutePos(colorizerRel);
        IColorizer colorizer = DecorBlocks.COLORIZER.get();
        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        player.getAbilities().instabuild = true;

        helper.startSequence()
                // An empty colorizer is a whole block and stops light itself, so glass is what lets
                // the light in. Without this the rest proves nothing: it says the sealed probe really
                // is lit through the colorizer while it holds glass.
                .thenExecute(() -> helper.assertTrue(
                        colorizer.setColorizer(level, colorizerPos, Blocks.GLASS.defaultBlockState(), player, InteractionHand.MAIN_HAND, false),
                        "setColorizer refused an empty colorizer"))
                .thenWaitUntil(() -> helper.assertTrue(level.getBrightness(LightLayer.BLOCK, probePos) > 0,
                        "no light reached the sealed probe through a glass filled colorizer"))
                .thenExecute(() -> helper.assertTrue(
                        colorizer.setColorizer(level, colorizerPos, Blocks.STONE.defaultBlockState(), player, InteractionHand.MAIN_HAND, false),
                        "setColorizer refused a filled colorizer"))
                .thenWaitUntil(() -> helper.assertValueEqual(level.getBrightness(LightLayer.BLOCK, probePos), 0,
                        "the light a stone filled colorizer let through to a sealed probe"))
                .thenSucceed();
    }

    /**
     * A filled colorizer answers the light questions with the block it stands in for, and only the
     * full cube does: a stairs colorizer keeps its own dampening, as in 1.20.1, or a colorized
     * staircase would cast the shadow of a solid block. Every answer is stated as "the same answer
     * the real block gives", which is the whole point of the feature and keeps absolute light
     * values, which a concurrently running neighbouring test can influence, out of it:
     * <ul>
     * <li>the dampening the light engines are handed per position,</li>
     * <li>whether skylight passes, as the library reports it, and</li>
     * <li>the skylight heightmap, which decides where a column stops seeing sky. A colorizer's block
     * state does not change when its stored block does, so the column has to be recomputed when it
     * is filled. Glass is the telling case: an empty colorizer already stops the column, being a
     * whole block, so only a stored block that lets the sky through can move the answer.</li>
     * </ul>
     */
    private static void colorizerTakesItsBlocksLightDampening(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos colorizerRel = new BlockPos(2, 1, 2);
        BlockPos stairsRel = new BlockPos(4, 1, 2);
        BlockPos referenceRel = new BlockPos(6, 1, 2);
        BlockPos colorizerPos = helper.absolutePos(colorizerRel);
        BlockPos stairsPos = helper.absolutePos(stairsRel);
        BlockPos referencePos = helper.absolutePos(referenceRel);

        helper.setBlock(colorizerRel, DecorBlocks.COLORIZER.get());
        helper.setBlock(stairsRel, DecorBlocks.COLORIZER_STAIRS.get());
        helper.runBeforeTestEnd(() -> {
            helper.setBlock(colorizerRel, Blocks.AIR);
            helper.setBlock(stairsRel, Blocks.AIR);
            helper.setBlock(referenceRel, Blocks.AIR);
        });

        IColorizer colorizer = DecorBlocks.COLORIZER.get();
        IColorizer stairs = DecorBlocks.COLORIZER_STAIRS.get();
        BlockState colorizerState = level.getBlockState(colorizerPos);
        BlockState stairsState = level.getBlockState(stairsPos);
        helper.assertValueEqual(colorizer.getLightDampening(colorizerState, level, colorizerPos), colorizerState.getLightDampening(),
                "an empty colorizer's light dampening, which should still be its own");

        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        player.getAbilities().instabuild = true;

        for (Block block : List.of(Blocks.STONE, Blocks.GLASS)) {
            BlockState stored = block.defaultBlockState();
            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
            helper.setBlock(referenceRel, block);
            helper.assertTrue(colorizer.setColorizer(level, colorizerPos, stored, player, InteractionHand.MAIN_HAND, false),
                    "setColorizer refused the colorizer");
            helper.assertTrue(stairs.setColorizer(level, stairsPos, stored, player, InteractionHand.MAIN_HAND, false),
                    "setColorizer refused the colorizer stairs");

            // Re-read: a colorizer may carry the answer in its block state, which filling replaces.
            BlockState filledColorizer = level.getBlockState(colorizerPos);
            BlockState filledStairs = level.getBlockState(stairsPos);
            helper.assertValueEqual(colorizer.getLightDampening(filledColorizer, level, colorizerPos), stored.getLightDampening(),
                    "a " + name + " filled colorizer's light dampening, against the real block's");
            helper.assertValueEqual(filledColorizer.getLightDampening(), stored.getLightDampening(),
                    "a " + name + " filled colorizer's baked light dampening, which is what the light engines read");
            helper.assertValueEqual(stairs.getLightDampening(filledStairs, level, stairsPos), stairsState.getLightDampening(),
                    "a " + name + " filled colorizer stairs' light dampening, which should still be its own");
            helper.assertValueEqual(Services.LEVEL_PROPERTIES.propagatesSkylightDown(level, colorizerPos),
                    Services.LEVEL_PROPERTIES.propagatesSkylightDown(level, referencePos),
                    "whether skylight passes a " + name + " filled colorizer, against the real block");
            helper.assertValueEqual(Services.LEVEL_PROPERTIES.propagatesSkylightDown(level, stairsPos),
                    Services.LEVEL_PROPERTIES.propagatesSkylightDown(level, referencePos),
                    "whether skylight passes " + name + " filled colorizer stairs, against the real block");
            helper.assertValueEqual(lowestSkySource(level, colorizerPos), lowestSkySource(level, referencePos),
                    "where the sky stops reaching down a " + name + " filled colorizer's column, against the real block's");
        }

        helper.succeed();
    }

    private static int lowestSkySource(ServerLevel level, BlockPos pos) {
        return level.getChunkAt(pos).getSkyLightSources().getLowestSourceY(pos.getX() & 15, pos.getZ() & 15);
    }

    /**
     * Lighting a fireplace makes it emit light, and putting it out stops it. Guards
     * {@code getLightEmission(state, level, pos)} reaching the light engine on both loaders.
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
                // The one check against the light engine, since the bug was emission never reaching
                // it. A lower bound, so light from a neighbouring test can only raise it.
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
}

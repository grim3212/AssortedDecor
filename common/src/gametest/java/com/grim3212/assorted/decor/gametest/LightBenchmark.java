package com.grim3212.assorted.decor.gametest;

import com.grim3212.assorted.decor.api.colorizer.IColorizer;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Times the light engine over a 7x7x7 cube of colorizers against the same cube of real blocks, on
 * its own synchronous {@link LevelLightEngine} so the propagation itself can be timed on the server
 * thread. Logs one line per scenario; asserts only that a filled colorizer cube lights the same as
 * its real block's cube.
 */
final class LightBenchmark {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int WARMUP = 100;
    private static final int RUNS = 300;

    private LightBenchmark() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("light_benchmark", LightBenchmark::run);
    }

    private record Scenario(String name, Block block, @Nullable BlockState stored) {
    }

    private record Result(String name, double placeMs, double fillMs, double toggleMedianUs, double toggleMeanUs, double skyMedianUs, double skyMeanUs, int probe) {
    }

    private static void run(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos centreRel = new BlockPos(4, 4, 4);
        BlockPos probeRel = new BlockPos(1, 4, 4);
        List<BlockPos> cube = new ArrayList<>();
        for (int x = 1; x <= 7; x++) {
            for (int y = 1; y <= 7; y++) {
                for (int z = 1; z <= 7; z++) {
                    BlockPos rel = new BlockPos(x, y, z);
                    if (!rel.equals(centreRel)) {
                        cube.add(rel);
                    }
                }
            }
        }
        helper.runBeforeTestEnd(() -> {
            cube.forEach(rel -> helper.setBlock(rel, Blocks.AIR));
            helper.setBlock(centreRel, Blocks.AIR);
        });

        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        player.getAbilities().instabuild = true;
        IColorizer colorizer = DecorBlocks.COLORIZER.get();

        List<Scenario> scenarios = List.of(
                new Scenario("stone", Blocks.STONE, null),
                new Scenario("glass", Blocks.GLASS, null),
                new Scenario("colorizer_empty", DecorBlocks.COLORIZER.get(), null),
                new Scenario("colorizer_stone", DecorBlocks.COLORIZER.get(), Blocks.STONE.defaultBlockState()),
                new Scenario("colorizer_glass", DecorBlocks.COLORIZER.get(), Blocks.GLASS.defaultBlockState()));

        // Two passes, reporting the second: the first scenario of a pass otherwise pays for the JIT
        // on behalf of the rest.
        List<Result> results = new ArrayList<>();
        for (int pass = 0; pass < 2; pass++) {
        results.clear();
        for (Scenario scenario : scenarios) {
            long placeStart = System.nanoTime();
            cube.forEach(rel -> helper.setBlock(rel, scenario.block()));
            long placeNs = System.nanoTime() - placeStart;

            long fillNs = 0;
            if (scenario.stored() != null) {
                long fillStart = System.nanoTime();
                cube.forEach(rel -> colorizer.setColorizer(level, helper.absolutePos(rel), scenario.stored(), player, InteractionHand.MAIN_HAND, false));
                fillNs = System.nanoTime() - fillStart;
            }

            BlockPos centre = helper.absolutePos(centreRel);
            BlockPos probe = helper.absolutePos(probeRel);
            LevelLightEngine engine = newBlockLightEngine(level, helper.absolutePos(new BlockPos(0, 0, 0)), helper.absolutePos(new BlockPos(8, 8, 8)));

            long[] toggles = new long[RUNS];
            int probeValue = -1;
            for (int i = 0; i < WARMUP + RUNS; i++) {
                helper.setBlock(centreRel, Blocks.GLOWSTONE);
                engine.checkBlock(centre);
                long start = System.nanoTime();
                engine.runLightUpdates();
                long on = System.nanoTime() - start;
                probeValue = engine.getLayerListener(LightLayer.BLOCK).getLightValue(probe);

                helper.setBlock(centreRel, Blocks.AIR);
                engine.checkBlock(centre);
                start = System.nanoTime();
                engine.runLightUpdates();
                long off = System.nanoTime() - start;
                if (i >= WARMUP) {
                    toggles[i - WARMUP] = on + off;
                }
            }

            LevelChunk chunk = level.getChunkAt(centre);
            long[] sky = new long[RUNS];
            for (int i = 0; i < WARMUP + RUNS; i++) {
                ChunkSkyLightSources sources = new ChunkSkyLightSources(level);
                long start = System.nanoTime();
                sources.fillFrom(chunk);
                long took = System.nanoTime() - start;
                if (i >= WARMUP) {
                    sky[i - WARMUP] = took;
                }
            }

            results.add(new Result(scenario.name(), placeNs / 1e6, fillNs / 1e6, median(toggles) / 1e3, mean(toggles) / 1e3, median(sky) / 1e3, mean(sky) / 1e3, probeValue));
            cube.forEach(rel -> helper.setBlock(rel, Blocks.AIR));
        }
        }

        LOGGER.info("[light_benchmark] {} warmup, {} timed iterations; toggle = glowstone on+off through the cube; sky = ChunkSkyLightSources#fillFrom of the chunk", WARMUP, RUNS);
        LOGGER.info("[light_benchmark] {}", String.format("%-16s %9s %9s %12s %12s %12s %12s %6s", "scenario", "place ms", "fill ms", "toggle med", "toggle mean", "sky med", "sky mean", "probe"));
        for (Result r : results) {
            LOGGER.info("[light_benchmark] {}", String.format("%-16s %9.2f %9.2f %10.1fus %10.1fus %10.1fus %10.1fus %6d",
                    r.name(), r.placeMs(), r.fillMs(), r.toggleMedianUs(), r.toggleMeanUs(), r.skyMedianUs(), r.skyMeanUs(), r.probe()));
        }

        helper.assertValueEqual(probe(results, "colorizer_stone"), probe(results, "stone"), "light through a stone filled colorizer cube, against real stone");
        helper.assertValueEqual(probe(results, "colorizer_glass"), probe(results, "glass"), "light through a glass filled colorizer cube, against real glass");
        helper.assertTrue(probe(results, "glass") > 0, "the glass cube let no light reach the probe, so the benchmark measured nothing");
        helper.succeed();
    }

    /** A block light engine of our own over the level's chunks, with light enabled in every section the box touches. */
    private static LevelLightEngine newBlockLightEngine(ServerLevel level, BlockPos min, BlockPos max) {
        LevelLightEngine engine = new LevelLightEngine(level.getChunkSource(), true, false);
        for (int cx = SectionPos.blockToSectionCoord(min.getX()); cx <= SectionPos.blockToSectionCoord(max.getX()); cx++) {
            for (int cz = SectionPos.blockToSectionCoord(min.getZ()); cz <= SectionPos.blockToSectionCoord(max.getZ()); cz++) {
                for (int sy = level.getMinSectionY(); sy <= level.getMaxSectionY(); sy++) {
                    engine.updateSectionStatus(SectionPos.of(cx, sy, cz), false);
                }
                engine.setLightEnabled(new ChunkPos(cx, cz), true);
                engine.propagateLightSources(new ChunkPos(cx, cz));
            }
        }
        engine.runLightUpdates();
        return engine;
    }

    private static int probe(List<Result> results, String name) {
        return results.stream().filter(r -> r.name().equals(name)).findFirst().orElseThrow().probe();
    }

    private static double median(long[] samples) {
        long[] sorted = samples.clone();
        Arrays.sort(sorted);
        return sorted[sorted.length / 2];
    }

    private static double mean(long[] samples) {
        return Arrays.stream(samples).average().orElse(0);
    }
}

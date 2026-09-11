package com.grim3212.assorted.decor.gametest;

import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.FluroBlock;
import com.grim3212.assorted.decor.common.blocks.IlluminationTubeBlock;
import com.grim3212.assorted.decor.common.blocks.LanternBlock;
import com.grim3212.assorted.decor.common.blocks.RoadwayLightBlock;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerFireplaceBaseBlock;
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

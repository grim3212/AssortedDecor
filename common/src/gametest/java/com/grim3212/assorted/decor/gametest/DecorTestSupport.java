package com.grim3212.assorted.decor.gametest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grim3212.assorted.decor.common.blocks.ColorChangingBlock;
import com.grim3212.assorted.decor.common.blocks.colorizer.ColorizerLampPost;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
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

/**
 * Helpers, constants and fixtures shared by AssortedDecor's gametest classes, which import them statically.
 */
final class DecorTestSupport {

    private DecorTestSupport() {
    }

    static final BlockPos MAIN = new BlockPos(4, 1, 4);

    /** Places one dyed siding on top of {@code floor} and checks it kept both its block and its colour. */
    static void placeSidingFromItem(GameTestHelper helper, Player player, BlockPos floor, Block siding, DyeColor color) {
        BlockPos placed = floor.above();
        helper.setBlock(floor, Blocks.STONE);

        ItemStack stack = ColorChangingBlock.getColorStack(new ItemStack(siding), color);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(floor))));

        helper.assertBlockPresent(siding, placed);
        helper.assertBlockProperty(placed, ColorChangingBlock.COLOR, color);
    }

    /** Every variant object in a blockstate json: each "variants" entry and each multipart "apply". */
    static List<JsonObject> blockstateVariants(JsonObject blockstate) {
        List<JsonObject> out = new ArrayList<>();
        if (blockstate.has("variants")) {
            for (Map.Entry<String, JsonElement> entry : blockstate.getAsJsonObject("variants").entrySet()) {
                addVariants(entry.getValue(), out);
            }
        }
        if (blockstate.has("multipart")) {
            for (JsonElement part : blockstate.getAsJsonArray("multipart")) {
                addVariants(part.getAsJsonObject().get("apply"), out);
            }
        }
        return out;
    }

    static void addVariants(JsonElement element, List<JsonObject> out) {
        if (element == null) {
            return;
        }
        if (element.isJsonArray()) {
            element.getAsJsonArray().forEach(variant -> out.add(variant.getAsJsonObject()));
        } else {
            out.add(element.getAsJsonObject());
        }
    }

    /**
     * Whether {@code key} names something in the language file. A suffixed key counts: a colour
     * changing item like siding overrides {@code getName} to translate
     * {@code <descriptionId>_<colour>}, so its bare description id is never meant to be there.
     */
    static boolean hasName(JsonObject lang, String key) {
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

    static JsonObject readJson(String path) {
        try (InputStream in = DecorTestSupport.class.getResourceAsStream(path)) {
            return in == null ? null : JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    static boolean resourceExists(String path) {
        try (InputStream in = DecorTestSupport.class.getResourceAsStream(path)) {
            return in != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Puts a colorizer down ready to be filled. A door needs both halves and a lamp post all three
     * parts before it takes a block, and the side-attached shapes need a wall to hang on.
     */
    static void placeShape(GameTestHelper helper, BlockPos rel, BlockState state) {
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
     * The light the block at {@code rel} declares for its own state and position. Tests assert this
     * rather than the light engine's brightness, which light from concurrently running neighbouring
     * test boxes raises.
     */
    static int lightEmission(GameTestHelper helper, BlockPos rel) {
        return helper.getLevel().getLightEmission(helper.absolutePos(rel));
    }

    /** A right click on the top face of {@code pos}, through the path that fires the loader's use-block event. */
    static InteractionResult rightClick(ServerPlayer player, ServerLevel level, ItemStack stack, BlockPos pos) {
        return player.gameMode.useItemOn(player, level, stack, InteractionHand.MAIN_HAND, hitTop(pos));
    }

    static BlockHitResult hitTop(BlockPos pos) {
        return hitSide(pos, Direction.UP);
    }

    static BlockHitResult hitSide(BlockPos pos, Direction face) {
        return new BlockHitResult(Vec3.atCenterOf(pos).relative(face, 0.5D), face, pos, false);
    }
}

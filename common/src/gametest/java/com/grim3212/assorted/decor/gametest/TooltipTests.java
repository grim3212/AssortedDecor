package com.grim3212.assorted.decor.gametest;

import com.grim3212.assorted.decor.common.items.DecorDataComponents;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Item tooltip lines, which come from data components. NeoForge also builds the full tooltip on the
 * server, so there it is checked too; {@code DecorClientGameTests} covers Fabric.
 */
final class TooltipTests {

    private TooltipTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("colorizer_brush_tooltip_names_its_block", TooltipTests::colorizerBrushTooltipNamesItsBlock);
    }

    /** The brush's {@code colorizer_brush_info} line says it is empty, then names what it picked up. */
    private static void colorizerBrushTooltipNamesItsBlock(GameTestHelper helper) {
        ItemStack brush = new ItemStack(DecorItems.COLORIZER_BRUSH.get());
        assertLine(helper, brush, "tooltip.colorizer_brush.empty");

        NBTHelper.putTag(brush, "stored_state", NbtUtils.writeBlockState(Blocks.GOLD_BLOCK.defaultBlockState()));
        assertLine(helper, brush, "tooltip.colorizer_brush.stored");
        helper.succeed();
    }

    private static void assertLine(GameTestHelper helper, ItemStack stack, String key) {
        Item.TooltipContext context = Item.TooltipContext.of(helper.getLevel());
        List<Component> lines = new ArrayList<>();
        stack.addToTooltip(DecorDataComponents.COLORIZER_BRUSH_INFO.get(), context, TooltipDisplay.DEFAULT, lines::add, TooltipFlag.NORMAL);
        helper.assertValueEqual(lines.stream().map(TooltipTests::key).toList(), List.of(key), "the brush's tooltip");

        if ("Forge".equals(Services.PLATFORM.getPlatformName())) {
            List<String> full = stack.getTooltipLines(context, null, TooltipFlag.NORMAL).stream().map(TooltipTests::key).toList();
            helper.assertTrue(full.contains(key), key + " is missing from the brush's tooltip " + full);
        }
    }

    private static String key(Component line) {
        return line.getContents() instanceof TranslatableContents translatable ? translatable.getKey() : line.getString();
    }
}

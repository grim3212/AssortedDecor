package com.grim3212.assorted.decor.gametest;

import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.lib.core.inventory.IMenuDataProvider;
import com.grim3212.assorted.lib.core.inventory.MenuData;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.GameType;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Menus built on the client.
 */
final class MenuTests {

    private static final BlockPos CAGE = new BlockPos(4, 1, 4);

    private MenuTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("cage_menu_rebuilds_on_the_client", MenuTests::cageMenuRebuildsOnTheClient);
    }

    /**
     * The cage's client menu needs nothing from the server, so it opens as a vanilla menu: the
     * client builds it from the menu type alone, and it matches what the server opened.
     */
    private static void cageMenuRebuildsOnTheClient(GameTestHelper helper) {
        helper.setBlock(CAGE, DecorBlocks.CAGE.get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        MenuProvider provider = helper.getBlockState(CAGE).getMenuProvider(helper.getLevel(), helper.absolutePos(CAGE));
        if (provider == null) {
            helper.fail("the cage has no menu provider");
            return;
        }
        helper.assertFalse(provider instanceof IMenuDataProvider<?>, "the cage provides menu data it does not need");

        AbstractContainerMenu server = provider.createMenu(1, player.getInventory(), player);
        helper.assertFalse(MenuData.hasData(server.getType()), "the cage opens a menu type that expects data");

        AbstractContainerMenu client = server.getType().create(1, player.getInventory());
        helper.assertTrue(client.getClass() == server.getClass(), "the cage built a " + client.getClass().getSimpleName() + " on the client");
        helper.assertValueEqual(client.slots.size(), server.slots.size(), "cage client menu slots");

        helper.succeed();
    }
}

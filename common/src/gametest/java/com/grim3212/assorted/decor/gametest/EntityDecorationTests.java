package com.grim3212.assorted.decor.gametest;

import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.blockentity.CageBlockEntity;
import com.grim3212.assorted.decor.common.entity.DecorEntityTypes;
import com.grim3212.assorted.decor.common.entity.FrameEntity;
import com.grim3212.assorted.decor.common.entity.WallpaperEntity;
import com.grim3212.assorted.decor.common.items.DecorItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;
import static com.grim3212.assorted.decor.gametest.DecorTestSupport.*;

/**
 * Decorations that are entities or hold one: the cage, frames and wallpaper.
 */
final class EntityDecorationTests {

    private EntityDecorationTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("cage_holds_mob_and_drops_it", EntityDecorationTests::cageHoldsMobAndDropsIt);
        out.accept("frame_places_and_drops", EntityDecorationTests::framePlacesAndDrops);
        out.accept("wallpaper_places_and_drops", EntityDecorationTests::wallpaperPlacesAndDrops);
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
        // The display mob is never added to a level, so it has to carry a spawner's display id: on a
        // client level every entity is built with id 0, and Entity#getId throws on that. Rendering
        // reaches it for every living entity, so an unmarked mob crashes the client (see
        // CageBlockEntity#storeEntity).
        helper.assertValueEqual(caged.getId(), -1, "the caged mob's entity id, which marks it as a display entity,");
        helper.assertTrue(level.getBlockState(pos).getAnalogOutputSignal(level, pos, Direction.UP) > 0,
                "a stocked cage gave a comparator nothing to read");

        helper.destroyBlock(MAIN);
        helper.succeedWhen(() -> helper.assertItemEntityPresent(Items.PIG_SPAWN_EGG, MAIN, 3.0D));
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
}

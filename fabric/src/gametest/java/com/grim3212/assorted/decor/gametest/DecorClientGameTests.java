package com.grim3212.assorted.decor.gametest;

import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.blockentity.CageBlockEntity;
import com.grim3212.assorted.decor.common.blocks.blockentity.ColorizerBlockEntity;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * How a colorizer is drawn, as an item and as a placed block, the brush's tooltip as Fabric builds
 * it, and that a caged mob survives render state extraction: what a headless server cannot see. Run
 * with {@code ./gradlew :fabric:runClientGameTest}; it exits non-zero on a failure. Each drawing
 * check compares the particle, which the item and block paths both take from the same model data.
 */
public class DecorClientGameTests implements FabricClientGameTest {

    private static final Identifier GOLD = Identifier.withDefaultNamespace("block/gold_block");

    @Override
    public void runTest(ClientGameTestContext context) {
        // Inside a world: an ItemStack cannot be made on the title screen, because an item's default
        // components are only bound once a world's registries have loaded.
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            context.runOnClient(client -> {
                // A colorizer item holding gold throws gold particles when it is dropped, broken or
                // eaten by lava - the particle comes from what the item draws, not the empty colorizer.
                ItemStack filled = new ItemStack(DecorBlocks.COLORIZER.get());
                NBTHelper.putTag(filled, "stored_state", NbtUtils.writeBlockState(Blocks.GOLD_BLOCK.defaultBlockState()));
                Identifier filledParticle = itemParticle(client, filled);
                if (!GOLD.equals(filledParticle)) {
                    throw new AssertionError("a colorizer item holding gold throws " + filledParticle + " particles, not " + GOLD);
                }

                // So the check above can only pass by reading the stack.
                Identifier emptyParticle = itemParticle(client, new ItemStack(DecorBlocks.COLORIZER.get()));
                if (GOLD.equals(emptyParticle)) {
                    throw new AssertionError("an empty colorizer item throws gold particles");
                }

                // Fabric only adds component tooltip lines on the client.
                ItemStack brush = new ItemStack(DecorItems.COLORIZER_BRUSH.get());
                NBTHelper.putTag(brush, "stored_state", NbtUtils.writeBlockState(Blocks.GOLD_BLOCK.defaultBlockState()));
                List<String> brushTooltip = brush.getTooltipLines(Item.TooltipContext.of(client.level), client.player, TooltipFlag.NORMAL).stream()
                        .map(line -> line.getContents() instanceof TranslatableContents translatable ? translatable.getKey() : line.getString())
                        .toList();
                if (!brushTooltip.contains("tooltip.colorizer_brush.stored")) {
                    throw new AssertionError("a brush holding gold has the tooltip " + brushTooltip);
                }
            });

            // A placed colorizer, filled on the server and synced to the client, has to break into
            // gold as well: this goes through the blockstate's assortedlib:specification type and the
            // model json's loader, both of which Fabric only reads from "fabric:type".
            BlockPos pos = world.getServer().computeOnServer(server -> {
                ServerLevel level = server.overworld();
                BlockPos at = server.getPlayerList().getPlayers().get(0).blockPosition().above(2);
                level.setBlockAndUpdate(at, DecorBlocks.COLORIZER.get().defaultBlockState());
                ((ColorizerBlockEntity) level.getBlockEntity(at)).setStoredBlockState(Blocks.GOLD_BLOCK.defaultBlockState());
                return at;
            });
            context.waitFor(client -> client.level != null
                    && client.level.getBlockEntity(pos) instanceof ColorizerBlockEntity colorizer
                    && colorizer.getStoredBlockState().is(Blocks.GOLD_BLOCK));

            Identifier placedParticle = context.computeOnClient(client -> {
                BlockState state = client.level.getBlockState(pos);
                return client.getModelManager().getBlockStateModelSet().getParticleMaterial(state, client.level, pos).sprite().contents().name();
            });
            if (!GOLD.equals(placedParticle)) {
                throw new AssertionError("a placed colorizer holding gold breaks into " + placedParticle + " particles, not " + GOLD);
            }

            // A caged mob is built on the client and drawn without ever being added to the level, so
            // it is never given an entity id there - Level#getNextEntityId answers 0 off the server.
            // Extracting a living entity's render state reads the id for its head slot whether or not
            // anything is worn, and Entity#getId throws on 0, so an unmarked mob crashes the client
            // the moment the cage is stocked. Only a client can see this: on a server the mob is
            // handed a real id.
            BlockPos cagePos = world.getServer().computeOnServer(server -> {
                ServerLevel level = server.overworld();
                BlockPos at = server.getPlayerList().getPlayers().get(0).blockPosition().above(4);
                level.setBlockAndUpdate(at, DecorBlocks.CAGE.get().defaultBlockState());
                ((CageBlockEntity) level.getBlockEntity(at)).getItemStackStorageHandler()
                        .setStackInSlot(0, new ItemStack(Items.PIG_SPAWN_EGG));
                return at;
            });
            context.waitFor(client -> client.level != null
                    && client.level.getBlockEntity(cagePos) instanceof CageBlockEntity cage
                    && cage.getItemStackStorageHandler().getStackInSlot(0).is(Items.PIG_SPAWN_EGG));

            context.runOnClient(client -> {
                Entity caged = ((CageBlockEntity) client.level.getBlockEntity(cagePos)).getCachedEntity();
                if (caged == null) {
                    throw new AssertionError("a stocked cage built no mob on the client");
                }
                // The call the cage's renderer makes; it threw ReportedException before the mob was
                // marked as a display entity.
                client.getEntityRenderDispatcher().extractEntity(caged, 0.0F);
            });
        }
    }

    /** The particle sprite an item throws off, resolved exactly as a dropped item is drawn. */
    private static Identifier itemParticle(Minecraft client, ItemStack stack) {
        ItemStackRenderState state = new ItemStackRenderState();
        client.getItemModelResolver().updateForTopItem(state, stack, ItemDisplayContext.GROUND, null, null, 0);
        Material.Baked particle = state.pickParticleMaterial(RandomSource.create(0L));
        return particle == null ? null : particle.sprite().contents().name();
    }
}

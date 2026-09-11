package com.grim3212.assorted.decor.gametest;

import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.blockentity.ColorizerBlockEntity;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * How a colorizer is drawn, as an item and as a placed block, which a headless server cannot see.
 * Run with {@code ./gradlew :fabric:runClientGameTest}; it exits non-zero on a failure. Each check
 * compares the particle, which the item and block paths both take from the same model data.
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

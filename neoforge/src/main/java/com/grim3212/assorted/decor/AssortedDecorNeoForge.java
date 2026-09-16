package com.grim3212.assorted.decor;

import com.grim3212.assorted.decor.client.data.DecorLanguageProvider;
import com.grim3212.assorted.decor.client.data.DecorManualProvider;
import com.grim3212.assorted.decor.client.data.DecorBlockstateProvider;
import com.grim3212.assorted.decor.client.data.DecorItemModelProvider;
import com.grim3212.assorted.decor.common.blocks.blockentity.DecorBlockEntityTypes;
import com.grim3212.assorted.decor.data.DecorBlockLoot;
import com.grim3212.assorted.decor.data.DecorBlockTagProvider;
import com.grim3212.assorted.decor.data.DecorItemTagProvider;
import com.grim3212.assorted.decor.data.DecorRecipes;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import com.grim3212.assorted.lib.inventory.ForgePlatformInventoryStorageHandlerUnsided;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedDecorNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedDecorNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);
        modBus.addListener(this::registerCapabilities);

        DecorCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Recipe providers are not data providers any more - the Runner owns the output.
        event.addProvider(new DecorRecipes.Runner(packOutput, lookupProvider));
        ForgeBlockTagProvider blockTagProvider = event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new DecorBlockTagProvider(packOutput, lookupProvider)));
        event.addProvider(new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, new DecorItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        event.addProvider(new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(DecorBlockLoot::new, LootContextParamSets.BLOCK)), lookupProvider));
    }

    /**
     * Client datagen: block states and models, item models and the lang file. The two model
     * providers split the mod between them so they never write the same file.
     */
    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new DecorBlockstateProvider(packOutput));
        event.addProvider(new DecorItemModelProvider(packOutput));
        event.addProvider(new DecorLanguageProvider(packOutput));
        event.addProvider(new DecorManualProvider(packOutput));
    }

    /**
     * Exposes the cage's inventory as {@code Capabilities.Item.BLOCK} through the library's unsided
     * handler, the NeoForge side of Fabric's {@code ItemStorage.SIDED}.
     */
    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, DecorBlockEntityTypes.CAGE.get(), (blockEntity, side) -> {
            if (blockEntity.isRemoved()) {
                return null;
            }
            return ((ForgePlatformInventoryStorageHandlerUnsided) blockEntity.getStorageHandler()).getCapability();
        });
    }
}

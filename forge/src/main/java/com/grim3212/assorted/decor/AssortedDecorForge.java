package com.grim3212.assorted.decor;

import com.grim3212.assorted.decor.client.data.ColorizerModelProvider;
import com.grim3212.assorted.decor.client.data.DecorBlockstateProvider;
import com.grim3212.assorted.decor.client.data.DecorItemModelProvider;
import com.grim3212.assorted.decor.client.data.DecorSpriteSourceProvider;
import com.grim3212.assorted.decor.data.DecorBlockLoot;
import com.grim3212.assorted.decor.data.DecorBlockTagProvider;
import com.grim3212.assorted.decor.data.DecorItemTagProvider;
import com.grim3212.assorted.decor.data.DecorRecipes;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedDecorForge {

    public AssortedDecorForge() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::gatherData);

        DecorCommonMod.init();
    }

    private void gatherData(final GatherDataEvent event) {
        DataGenerator datagenerator = event.getGenerator();
        PackOutput packOutput = datagenerator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Server
        datagenerator.addProvider(event.includeServer(), new DecorRecipes(packOutput));
        ForgeBlockTagProvider blockTagProvider = new ForgeBlockTagProvider(packOutput, lookupProvider, fileHelper, Constants.MOD_ID, new DecorBlockTagProvider(packOutput, lookupProvider));
        datagenerator.addProvider(event.includeServer(), blockTagProvider);
        datagenerator.addProvider(event.includeServer(), new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), fileHelper, Constants.MOD_ID, new DecorItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        datagenerator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(DecorBlockLoot::new, LootContextParamSets.BLOCK))));

        // Client
        ColorizerModelProvider loadedModels = new ColorizerModelProvider(packOutput, fileHelper);
        datagenerator.addProvider(event.includeClient(), new DecorBlockstateProvider(packOutput, fileHelper, loadedModels));
        datagenerator.addProvider(event.includeClient(), loadedModels);
        datagenerator.addProvider(event.includeClient(), new DecorItemModelProvider(packOutput, fileHelper));
        datagenerator.addProvider(event.includeClient(), new DecorSpriteSourceProvider(packOutput, fileHelper));
    }
}

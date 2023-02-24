package com.grim3212.assorted.decor;

import com.grim3212.assorted.decor.client.data.ColorizerModelProvider;
import com.grim3212.assorted.decor.client.data.DecorBlockstateProvider;
import com.grim3212.assorted.decor.client.data.DecorItemModelProvider;
import com.grim3212.assorted.decor.client.data.DecorSpriteSourceProvider;
import com.grim3212.assorted.decor.common.helpers.DecorCreativeItems;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.decor.data.DecorBlockLoot;
import com.grim3212.assorted.decor.data.DecorCommonRecipeProvider;
import com.grim3212.assorted.decor.data.ForgeBlockTagProvider;
import com.grim3212.assorted.decor.data.ForgeItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.compress.utils.Lists;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Mod(Constants.MOD_ID)
public class AssortedDecor {

    public AssortedDecor() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::gatherData);
        modBus.addListener(this::processIMC);
        modBus.addListener(this::registerTabs);

        DecorCommonMod.init();
    }

    public static List<Tuple<ResourceLocation, String>> cageItems = Lists.newArrayList();

    private void processIMC(final InterModProcessEvent event) {
        event.enqueueWork(() -> {
            InterModComms.getMessages(Constants.MOD_ID, (s) -> s.equalsIgnoreCase("addCageItem")).forEach(message -> {
                Supplier<List<Tuple<ResourceLocation, String>>> supplier = message.getMessageSupplier();
                supplier.get().forEach(item -> {
                    cageItems.add(item);
                    Constants.LOG.info("Registered {} for Cage support", item.getA());
                });
            });
        });
    }

    private void gatherData(final GatherDataEvent event) {
        DataGenerator datagenerator = event.getGenerator();
        PackOutput packOutput = datagenerator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        datagenerator.addProvider(event.includeServer(), new DecorCommonRecipeProvider(packOutput));
        ForgeBlockTagProvider blockTagProvider = new ForgeBlockTagProvider(packOutput, lookupProvider, fileHelper);
        datagenerator.addProvider(event.includeServer(), blockTagProvider);
        datagenerator.addProvider(event.includeServer(), new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider, fileHelper));
        datagenerator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(DecorBlockLoot::new, LootContextParamSets.BLOCK))));

        ColorizerModelProvider loadedModels = new ColorizerModelProvider(packOutput, fileHelper);
        datagenerator.addProvider(event.includeClient(), new DecorBlockstateProvider(packOutput, fileHelper, loadedModels));
        datagenerator.addProvider(event.includeClient(), loadedModels);
        datagenerator.addProvider(event.includeClient(), new DecorItemModelProvider(packOutput, fileHelper));
        datagenerator.addProvider(event.includeClient(), new DecorSpriteSourceProvider(packOutput, fileHelper));
    }

    private void registerTabs(final CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new ResourceLocation(Constants.MOD_ID, "tab"), builder -> builder.title(Component.translatable("itemGroup.assorteddecor")).icon(() -> new ItemStack(DecorItems.WALLPAPER.get())).displayItems((enabledFlags, populator, hasPermissions) -> {
            populator.acceptAll(DecorCreativeItems.getCreativeItems());
        }));
    }
}

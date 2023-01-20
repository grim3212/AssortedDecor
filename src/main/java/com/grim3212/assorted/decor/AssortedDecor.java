package com.grim3212.assorted.decor;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.grim3212.assorted.decor.client.data.ColorizerModelProvider;
import com.grim3212.assorted.decor.client.data.DecorBlockstateProvider;
import com.grim3212.assorted.decor.client.data.DecorItemModelProvider;
import com.grim3212.assorted.decor.client.data.DecorSpriteSourceProvider;
import com.grim3212.assorted.decor.client.proxy.ClientProxy;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.block.blockentity.DecorBlockEntityTypes;
import com.grim3212.assorted.decor.common.crafting.StoredFluidIngredient;
import com.grim3212.assorted.decor.common.creative.DecorCreativeTab;
import com.grim3212.assorted.decor.common.data.DecorBlockTagProvider;
import com.grim3212.assorted.decor.common.data.DecorItemTagProvider;
import com.grim3212.assorted.decor.common.data.DecorLootProvider;
import com.grim3212.assorted.decor.common.data.DecorLootProvider.BlockTables;
import com.grim3212.assorted.decor.common.data.DecorRecipes;
import com.grim3212.assorted.decor.common.entity.DecorEntityTypes;
import com.grim3212.assorted.decor.common.handler.DecorConfig;
import com.grim3212.assorted.decor.common.handler.TagLoadListener;
import com.grim3212.assorted.decor.common.inventory.DecorContainerTypes;
import com.grim3212.assorted.decor.common.item.DecorItems;
import com.grim3212.assorted.decor.common.network.PacketHandler;
import com.grim3212.assorted.decor.common.proxy.IProxy;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod(AssortedDecor.MODID)
public class AssortedDecor {
	public static final String MODID = "assorteddecor";
	public static final String MODNAME = "Assorted Decor";

	public static IProxy proxy = new IProxy() {
	};

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public AssortedDecor() {
		DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
		proxy.starting();

		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		modBus.addListener(this::setup);
		modBus.addListener(this::gatherData);
		modBus.addListener(this::processIMC);
		modBus.addListener(this::registerRecipeSerializers);
		modBus.addListener(DecorCreativeTab::registerTabs);

		MinecraftForge.EVENT_BUS.register(new TagLoadListener());

		DecorBlocks.BLOCKS.register(modBus);
		DecorItems.ITEMS.register(modBus);
		DecorBlockEntityTypes.BLOCK_ENTITIES.register(modBus);
		DecorEntityTypes.ENTITIES.register(modBus);
		DecorContainerTypes.CONTAINER_TYPES.register(modBus);

		ModLoadingContext.get().registerConfig(Type.CLIENT, DecorConfig.CLIENT_SPEC);
		ModLoadingContext.get().registerConfig(Type.COMMON, DecorConfig.COMMON_SPEC);
	}

	private void setup(final FMLCommonSetupEvent event) {
		PacketHandler.init();
	}

	public static List<Tuple<ResourceLocation, String>> cageItems = Lists.newArrayList();

	private void processIMC(final InterModProcessEvent event) {
		event.enqueueWork(() -> {
			InterModComms.getMessages(MODID, (s) -> s.equalsIgnoreCase("addCageItem")).forEach(message -> {
				Supplier<List<Tuple<ResourceLocation, String>>> supplier = message.getMessageSupplier();
				supplier.get().forEach(item -> {
					cageItems.add(item);
					LOGGER.info("Registered {} for Cage support", item.getA());
				});
			});
		});
	}

	private void gatherData(final GatherDataEvent event) {
		DataGenerator datagenerator = event.getGenerator();
		PackOutput packOutput = datagenerator.getPackOutput();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		datagenerator.addProvider(event.includeServer(), new DecorRecipes(packOutput));
		DecorBlockTagProvider blockTagProvider = new DecorBlockTagProvider(packOutput, lookupProvider, fileHelper);
		datagenerator.addProvider(event.includeServer(), blockTagProvider);
		datagenerator.addProvider(event.includeServer(), new DecorItemTagProvider(packOutput, lookupProvider, blockTagProvider, fileHelper));
		datagenerator.addProvider(event.includeServer(), new DecorLootProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(BlockTables::new, LootContextParamSets.BLOCK))));

		ColorizerModelProvider loadedModels = new ColorizerModelProvider(packOutput, fileHelper);
		datagenerator.addProvider(event.includeClient(), new DecorBlockstateProvider(packOutput, fileHelper, loadedModels));
		datagenerator.addProvider(event.includeClient(), loadedModels);
		datagenerator.addProvider(event.includeClient(), new DecorItemModelProvider(packOutput, fileHelper));
		datagenerator.addProvider(event.includeClient(), new DecorSpriteSourceProvider(packOutput, fileHelper));
	}

	private void registerRecipeSerializers(final RegisterEvent event) {
		if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
			CraftingHelper.register(new ResourceLocation(AssortedDecor.MODID, "fluid"), StoredFluidIngredient.Serializer.INSTANCE);
		}
	}
}

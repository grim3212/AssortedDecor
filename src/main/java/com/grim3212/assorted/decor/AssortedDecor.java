package com.grim3212.assorted.decor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.grim3212.assorted.decor.client.data.ColorizerModelProvider;
import com.grim3212.assorted.decor.client.data.DecorBlockstateProvider;
import com.grim3212.assorted.decor.client.data.DecorItemModelProvider;
import com.grim3212.assorted.decor.client.proxy.ClientProxy;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.block.blockentity.DecorBlockEntityTypes;
import com.grim3212.assorted.decor.common.data.DecorBlockTagProvider;
import com.grim3212.assorted.decor.common.data.DecorItemTagProvider;
import com.grim3212.assorted.decor.common.data.DecorLootProvider;
import com.grim3212.assorted.decor.common.data.DecorRecipes;
import com.grim3212.assorted.decor.common.entity.DecorEntityTypes;
import com.grim3212.assorted.decor.common.handler.DecorConfig;
import com.grim3212.assorted.decor.common.handler.TagLoadListener;
import com.grim3212.assorted.decor.common.item.DecorItems;
import com.grim3212.assorted.decor.common.network.PacketHandler;
import com.grim3212.assorted.decor.common.proxy.IProxy;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod(AssortedDecor.MODID)
public class AssortedDecor {
	public static final String MODID = "assorteddecor";
	public static final String MODNAME = "Assorted Decor";

	public static IProxy proxy = new IProxy() {
	};

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static final CreativeModeTab ASSORTED_DECOR_ITEM_GROUP = (new CreativeModeTab("assorteddecor") {
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(DecorItems.WALLPAPER.get());
		}
	});

	public AssortedDecor() {
		DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
		proxy.starting();

		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		modBus.addListener(this::setup);
		modBus.addListener(this::gatherData);

		MinecraftForge.EVENT_BUS.register(new TagLoadListener());

		DecorBlocks.BLOCKS.register(modBus);
		DecorItems.ITEMS.register(modBus);
		DecorBlockEntityTypes.BLOCK_ENTITIES.register(modBus);
		DecorEntityTypes.ENTITIES.register(modBus);

		ModLoadingContext.get().registerConfig(Type.CLIENT, DecorConfig.CLIENT_SPEC);
		ModLoadingContext.get().registerConfig(Type.COMMON, DecorConfig.COMMON_SPEC);

	}

	private void setup(final FMLCommonSetupEvent event) {
		PacketHandler.init();
	}

	private void gatherData(GatherDataEvent event) {
		DataGenerator datagenerator = event.getGenerator();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();

		if (event.includeServer()) {
			datagenerator.addProvider(new DecorRecipes(datagenerator));
			DecorBlockTagProvider blockTagProvider = new DecorBlockTagProvider(datagenerator, fileHelper);
			datagenerator.addProvider(blockTagProvider);
			datagenerator.addProvider(new DecorItemTagProvider(datagenerator, blockTagProvider, fileHelper));
			datagenerator.addProvider(new DecorLootProvider(datagenerator));
		}

		if (event.includeClient()) {
			ColorizerModelProvider loadedModels = new ColorizerModelProvider(datagenerator, fileHelper);
			datagenerator.addProvider(new DecorBlockstateProvider(datagenerator, fileHelper, loadedModels));
			datagenerator.addProvider(loadedModels);
			datagenerator.addProvider(new DecorItemModelProvider(datagenerator, fileHelper));
		}
	}
}

package com.grim3212.assorted.decor.client.proxy;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.client.model.WallpaperModel;
import com.grim3212.assorted.decor.client.render.entity.FrameRenderer;
import com.grim3212.assorted.decor.client.render.entity.WallpaperRenderer;
import com.grim3212.assorted.decor.common.entity.DecorEntities;
import com.grim3212.assorted.decor.common.proxy.IProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@EventBusSubscriber(value = Dist.CLIENT, modid = AssortedDecor.MODID, bus = Bus.MOD)
public class ClientProxy implements IProxy {

	@Override
	public void starting() {
		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::setupClient);

		if (Minecraft.getInstance() != null) {
			ModelLoaderRegistry.registerLoader(WallpaperModel.WallpaperLoader.LOCATION, new WallpaperModel.WallpaperLoader());
		}
	}

	private void setupClient(final FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(DecorEntities.WALLPAPER.get(), WallpaperRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(DecorEntities.FRAME.get(), FrameRenderer::new);
	}

	@SubscribeEvent
	public static void onTextureStitch(TextureStitchEvent.Pre evt) {
		if (!evt.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE))
			return;

		AssortedDecor.LOGGER.debug("Stitching textures to block atlas");

		for (int i = 0; i < 24; i++) {
			//evt.addSprite(new ResourceLocation(AssortedDecor.MODID, "block/wallpaper/wallpaper_" + i));
		}
	}
}

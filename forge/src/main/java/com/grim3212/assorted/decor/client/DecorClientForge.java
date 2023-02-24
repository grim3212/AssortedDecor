package com.grim3212.assorted.decor.client;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.client.model.ColorizerBlockModel;
import com.grim3212.assorted.decor.client.model.ColorizerObjModel;
import com.grim3212.assorted.decor.client.screen.CageScreen;
import com.grim3212.assorted.decor.common.inventory.DecorContainerTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DecorClientForge {

    @SuppressWarnings("removal")
    @SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(DecorContainerTypes.CAGE.get(), CageScreen::new);
        });

        DecorClient.setRenderTypes(ItemBlockRenderTypes::setRenderLayer);
    }

    @SubscribeEvent
    public static void registerLoaders(final ModelEvent.RegisterGeometryLoaders event) {
        event.register("models/colorizer", ColorizerBlockModel.Loader.INSTANCE);
        event.register("models/colorizer_obj", ColorizerObjModel.ColorizerObjLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        DecorClient.registerEntityRenderers(event::registerEntityRenderer);
        DecorClient.registerBlockEntityRenderers(event::registerBlockEntityRenderer);
    }

    @SubscribeEvent
    public static void registerBlockColorHandles(final RegisterColorHandlersEvent.Block event) {
        DecorClient.registerBlockColors(event.getBlockColors(), event::register);
    }

    @SubscribeEvent
    public static void registerItemColorHandles(final RegisterColorHandlersEvent.Item event) {
        DecorClient.registerItemColors(event.getItemColors(), event::register);
    }
}

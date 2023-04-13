package com.grim3212.assorted.decor.client;

import com.grim3212.assorted.decor.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AssortedDecorForgeClient {
    
    @SubscribeEvent
    public static void initClientSide(final FMLConstructModEvent event) {
        DecorClient.init();
    }
}

package com.grim3212.assorted.decor;

import com.grim3212.assorted.decor.client.DecorClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedDecorFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DecorClient.init();
    }
}

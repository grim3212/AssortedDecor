package com.grim3212.assorted.decor.common.network;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.platform.services.INetworkHelper;
import net.minecraft.resources.ResourceLocation;

public class DecorPackets {
    public static void init() {
        Services.NETWORK.register(new INetworkHelper.MessageHandler<>(resource("neon_sign_change_mode"), NeonChangeModePacket.class, NeonChangeModePacket::encode, NeonChangeModePacket::decode, NeonChangeModePacket::handle, INetworkHelper.MessageBoundSide.SERVER));
        Services.NETWORK.register(new INetworkHelper.MessageHandler<>(resource("neon_sign_open"), NeonOpenPacket.class, NeonOpenPacket::encode, NeonOpenPacket::decode, NeonOpenPacket::handle, INetworkHelper.MessageBoundSide.CLIENT));
        Services.NETWORK.register(new INetworkHelper.MessageHandler<>(resource("neon_sign_update"), NeonUpdatePacket.class, NeonUpdatePacket::encode, NeonUpdatePacket::decode, NeonUpdatePacket::handle, INetworkHelper.MessageBoundSide.SERVER));
    }

    private static ResourceLocation resource(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }
}

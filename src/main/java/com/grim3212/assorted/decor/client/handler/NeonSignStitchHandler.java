package com.grim3212.assorted.decor.client.handler;

import com.grim3212.assorted.decor.AssortedDecor;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AssortedDecor.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NeonSignStitchHandler {

	private static final ResourceLocation NEON_SIGN_TEXTURE = new ResourceLocation(AssortedDecor.MODID, "model/neon_sign");
	private static final ResourceLocation NEON_SIGN_CLEAR_TEXTURE = new ResourceLocation(AssortedDecor.MODID, "model/neon_sign_clear");
	private static final ResourceLocation NEON_SIGN_WHITE_TEXTURE = new ResourceLocation(AssortedDecor.MODID, "model/neon_sign_white");
	private static final ResourceLocation VANILLA_SIGN = new ResourceLocation("entity/signs/oak");

	public static ResourceLocation getSignTexture(int mode) {
		switch (mode) {
		case 0:
			return NEON_SIGN_TEXTURE;
		case 1:
			return NEON_SIGN_WHITE_TEXTURE;
		case 2:
			return NEON_SIGN_CLEAR_TEXTURE;
		default:
			return VANILLA_SIGN;
		}
	}

	@SubscribeEvent
	public static void onStitch(TextureStitchEvent.Pre event) {
		if (!event.getMap().location().equals(Sheets.SIGN_SHEET)) {
			return;
		}

		event.addSprite(NEON_SIGN_TEXTURE);
		event.addSprite(NEON_SIGN_CLEAR_TEXTURE);
		event.addSprite(NEON_SIGN_WHITE_TEXTURE);
	}

}

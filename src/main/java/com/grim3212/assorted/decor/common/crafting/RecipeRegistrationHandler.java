package com.grim3212.assorted.decor.common.crafting;

import com.grim3212.assorted.decor.AssortedDecor;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AssortedDecor.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RecipeRegistrationHandler {

	@SubscribeEvent
	public static void onRegister(RegistryEvent.Register<RecipeSerializer<?>> event) {
		CraftingHelper.register(IngredientBlockListIngredient.Serializer.ID, IngredientBlockListIngredient.Serializer.INSTANCE);
	}
}

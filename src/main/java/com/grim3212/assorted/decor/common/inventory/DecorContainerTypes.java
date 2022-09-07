package com.grim3212.assorted.decor.common.inventory;

import com.grim3212.assorted.decor.AssortedDecor;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DecorContainerTypes {
	public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AssortedDecor.MODID);

	public static final RegistryObject<MenuType<CageContainer>> CAGE = CONTAINER_TYPES.register("cage", () -> new MenuType<>(CageContainer::new));
}

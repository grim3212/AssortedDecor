package com.grim3212.assorted.decor.common.entity;

import com.grim3212.assorted.decor.AssortedDecor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DecorEntityTypes {

	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, AssortedDecor.MODID);

	public static final RegistryObject<EntityType<WallpaperEntity>> WALLPAPER = register("wallpaper", EntityType.Builder.<WallpaperEntity>create(WallpaperEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).setTrackingRange(250).setUpdateInterval(2147483647).setShouldReceiveVelocityUpdates(false));
	public static final RegistryObject<EntityType<FrameEntity>> FRAME = register("frame", EntityType.Builder.<FrameEntity>create(FrameEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).setTrackingRange(250).setUpdateInterval(2147483647).setShouldReceiveVelocityUpdates(false));

	private static <T extends Entity> RegistryObject<EntityType<T>> register(final String name, final EntityType.Builder<T> builder) {
		return ENTITIES.register(name, () -> builder.build(new ResourceLocation(AssortedDecor.MODID, name).toString()));
	}

}

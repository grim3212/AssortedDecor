package com.grim3212.assorted.decor.common.entity;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class DecorEntityTypes {

    public static final RegistryProvider<EntityType<?>> ENTITIES = RegistryProvider.create(Registries.ENTITY_TYPE, Constants.MOD_ID);

    public static final IRegistryObject<EntityType<WallpaperEntity>> WALLPAPER = register("wallpaper", EntityType.Builder.<WallpaperEntity>of(WallpaperEntity::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(250).updateInterval(2147483647));
    public static final IRegistryObject<EntityType<WoodFrameEntity>> WOOD_FRAME = register("wood_frame", EntityType.Builder.<WoodFrameEntity>of(WoodFrameEntity::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(250).updateInterval(2147483647));
    public static final IRegistryObject<EntityType<IronFrameEntity>> IRON_FRAME = register("iron_frame", EntityType.Builder.<IronFrameEntity>of(IronFrameEntity::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(250).updateInterval(2147483647));

    private static <T extends Entity> IRegistryObject<EntityType<T>> register(final String name, final EntityType.Builder<T> builder) {
        return ENTITIES.register(name, () -> builder.build(new ResourceLocation(Constants.MOD_ID, name).toString()));
    }

    public static void init() {
    }
}

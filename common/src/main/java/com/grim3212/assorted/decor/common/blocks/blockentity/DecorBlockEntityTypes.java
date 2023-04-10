package com.grim3212.assorted.decor.common.blocks.blockentity;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class DecorBlockEntityTypes {
    public static final RegistryProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistryProvider.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final IRegistryObject<BlockEntityType<ColorizerBlockEntity>> COLORIZER = BLOCK_ENTITIES.register("colorizer", () -> Services.PLATFORM.createBlockEntityType(ColorizerBlockEntity::new, DecorBlocks.colorizerBlocks().stream().map(IRegistryObject::get).toArray(Block[]::new)));
    public static final IRegistryObject<BlockEntityType<NeonSignBlockEntity>> NEON_SIGN = BLOCK_ENTITIES.register("neon_sign", () -> Services.PLATFORM.createBlockEntityType(NeonSignBlockEntity::new, DecorBlocks.NEON_SIGN.get(), DecorBlocks.NEON_SIGN_WALL.get()));
    public static final IRegistryObject<BlockEntityType<CalendarBlockEntity>> CALENDAR = BLOCK_ENTITIES.register("calendar", () -> Services.PLATFORM.createBlockEntityType(CalendarBlockEntity::new, DecorBlocks.CALENDAR.get()));
    public static final IRegistryObject<BlockEntityType<WallClockBlockEntity>> WALL_CLOCK = BLOCK_ENTITIES.register("wall_clock", () -> Services.PLATFORM.createBlockEntityType(WallClockBlockEntity::new, DecorBlocks.WALL_CLOCK.get()));
    public static final IRegistryObject<BlockEntityType<CageBlockEntity>> CAGE = BLOCK_ENTITIES.register("cage", () -> Services.PLATFORM.createBlockEntityType(CageBlockEntity::new, DecorBlocks.CAGE.get()));

    public static void init() {
    }
}

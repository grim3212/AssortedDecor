package com.grim3212.assorted.decor.common.block.blockentity;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.DecorBlocks;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DecorBlockEntityTypes {

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, AssortedDecor.MODID);

	public static final RegistryObject<BlockEntityType<ColorizerBlockEntity>> COLORIZER = BLOCK_ENTITIES.register("colorizer", () -> BlockEntityType.Builder.of(ColorizerBlockEntity::new, DecorBlocks.COLORIZER.get()).build(null));
	public static final RegistryObject<BlockEntityType<NeonSignBlockEntity>> NEON_SIGN = BLOCK_ENTITIES.register("neon_sign", () -> BlockEntityType.Builder.of(NeonSignBlockEntity::new, DecorBlocks.NEON_SIGN.get(), DecorBlocks.NEON_SIGN_WALL.get()).build(null));
	public static final RegistryObject<BlockEntityType<CalendarBlockEntity>> CALENDAR = BLOCK_ENTITIES.register("calendar", () -> BlockEntityType.Builder.of(CalendarBlockEntity::new, DecorBlocks.CALENDAR.get()).build(null));
	public static final RegistryObject<BlockEntityType<WallClockBlockEntity>> WALL_CLOCK = BLOCK_ENTITIES.register("wall_clock", () -> BlockEntityType.Builder.of(WallClockBlockEntity::new, DecorBlocks.WALL_CLOCK.get()).build(null));

}

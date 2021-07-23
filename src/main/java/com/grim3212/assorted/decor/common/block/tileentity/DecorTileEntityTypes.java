package com.grim3212.assorted.decor.common.block.tileentity;

import java.util.Set;

import com.google.common.collect.Sets;
import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.DecorBlocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DecorTileEntityTypes {

	public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, AssortedDecor.MODID);

	public static final RegistryObject<BlockEntityType<ColorizerTileEntity>> COLORIZER = TILE_ENTITIES.register("colorizer", () -> new BlockEntityType<>(ColorizerTileEntity::new, getColorizerBlocks(), null));
	public static final RegistryObject<BlockEntityType<NeonSignTileEntity>> NEON_SIGN = TILE_ENTITIES.register("neon_sign", () -> new BlockEntityType<>(NeonSignTileEntity::new, Sets.newHashSet(DecorBlocks.NEON_SIGN.get(), DecorBlocks.NEON_SIGN_WALL.get()), null));
	public static final RegistryObject<BlockEntityType<CalendarTileEntity>> CALENDAR = TILE_ENTITIES.register("calendar", () -> new BlockEntityType<>(CalendarTileEntity::new, Sets.newHashSet(DecorBlocks.CALENDAR.get()), null));
	public static final RegistryObject<BlockEntityType<WallClockTileEntity>> WALL_CLOCK = TILE_ENTITIES.register("wall_clock", () -> new BlockEntityType<>(WallClockTileEntity::new, Sets.newHashSet(DecorBlocks.WALL_CLOCK.get()), null));

	private static Set<Block> getColorizerBlocks() {
		return Sets.newHashSet(DecorBlocks.COLORIZER.get());
	}
}

package com.grim3212.assorted.decor.common.block.tileentity;

import java.util.Set;

import com.google.common.collect.Sets;
import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.DecorBlocks;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DecorTileEntityTypes {

	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, AssortedDecor.MODID);

	public static final RegistryObject<TileEntityType<ColorizerTileEntity>> COLORIZER = TILE_ENTITIES.register("colorizer", () -> new TileEntityType<>(ColorizerTileEntity::new, getColorizerBlocks(), null));
	public static final RegistryObject<TileEntityType<NeonSignTileEntity>> NEON_SIGN = TILE_ENTITIES.register("neon_sign", () -> new TileEntityType<>(NeonSignTileEntity::new, Sets.newHashSet(DecorBlocks.NEON_SIGN.get(), DecorBlocks.NEON_SIGN_WALL.get()), null));

	private static Set<Block> getColorizerBlocks() {
		return Sets.newHashSet(DecorBlocks.COLORIZER.get());
	}
}

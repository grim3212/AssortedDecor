package com.grim3212.assorted.decor.common.block;

import java.util.function.Function;
import java.util.function.Supplier;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.item.DecorItems;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DecorBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AssortedDecor.MODID);
	public static final DeferredRegister<Item> ITEMS = DecorItems.ITEMS;

	public static final RegistryObject<ColorizerBlock> COLORIZER = register("colorizer", () -> new ColorizerBlock(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.5f, 12.0f).notSolid().variableOpacity()));
	public static final RegistryObject<HardenedWoodBlock> HARDENED_WOOD = register("hardened_wood", () -> new HardenedWoodBlock(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.5f, 12.0f)));
	
	private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup) {
		return register(name, sup, block -> item(block));
	}

	private static <T extends Block> RegistryObject<T> registerWithItem(String name, Supplier<? extends T> sup, Supplier<BlockItem> blockItem) {
		return register(name, sup, block -> blockItem);
	}

	private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup, Function<RegistryObject<T>, Supplier<? extends Item>> itemCreator) {
		RegistryObject<T> ret = registerNoItem(name, sup);
		ITEMS.register(name, itemCreator.apply(ret));
		return ret;
	}

	private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<? extends T> sup) {
		return BLOCKS.register(name, sup);
	}

	private static Supplier<BlockItem> item(final RegistryObject<? extends Block> block) {
		return () -> new BlockItem(block.get(), new Item.Properties().group(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP));
	}
}

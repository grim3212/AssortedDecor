package com.grim3212.assorted.decor.common.block;

import java.util.function.Function;
import java.util.function.Supplier;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.item.DecorItems;
import com.grim3212.assorted.decor.common.util.DecorUtil.SlopeType;

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

	public static final RegistryObject<ColorizerBlock> COLORIZER = register("colorizer", () -> new ColorizerBlock());
	public static final RegistryObject<ColorizerBlock> COLORIZER_CHAIR = register("colorizer_chair", () -> new ColorizerChairBlock());
	public static final RegistryObject<ColorizerBlock> COLORIZER_TABLE = register("colorizer_table", () -> new ColorizerTableBlock());
	public static final RegistryObject<ColorizerBlock> COLORIZER_COUNTER = register("colorizer_counter", () -> new ColorizerCounterBlock());
	public static final RegistryObject<ColorizerBlock> COLORIZER_STOOL = register("colorizer_stool", () -> new ColorizerStoolBlock());

	public static final RegistryObject<ColorizerBlock> COLORIZER_SLOPE = register("colorizer_slope", () -> new ColorizerSlopeBlock(SlopeType.SLOPE));
	public static final RegistryObject<ColorizerBlock> COLORIZER_SLOPED_ANGLE = register("colorizer_sloped_angle", () -> new ColorizerSlopeBlock(SlopeType.SLOPED_ANGLE));
	public static final RegistryObject<ColorizerBlock> COLORIZER_SLOPED_INTERSECTION = register("colorizer_sloped_intersection", () -> new ColorizerSlopeBlock(SlopeType.SLOPED_INTERSECTION));
	public static final RegistryObject<ColorizerBlock> COLORIZER_OBLIQUE_SLOPE = register("colorizer_oblique_slope", () -> new ColorizerSlopeBlock(SlopeType.OBLIQUE_SLOPE));
	public static final RegistryObject<ColorizerBlock> COLORIZER_CORNER = register("colorizer_corner", () -> new ColorizerSlopeBlock(SlopeType.CORNER));
	public static final RegistryObject<ColorizerBlock> COLORIZER_SLANTED_CORNER = register("colorizer_slanted_corner", () -> new ColorizerSlopeBlock(SlopeType.SLANTED_CORNER));
	public static final RegistryObject<ColorizerBlock> COLORIZER_PYRAMID = register("colorizer_pyramid", () -> new ColorizerSlopeSideBlock(SlopeType.PYRAMID));
	public static final RegistryObject<ColorizerBlock> COLORIZER_FULL_PYRAMID = register("colorizer_full_pyramid", () -> new ColorizerSlopeSideBlock(SlopeType.FULL_PYRAMID));
	public static final RegistryObject<ColorizerBlock> COLORIZER_SLOPED_POST = register("colorizer_sloped_post", () -> new ColorizerSlopeSideBlock(SlopeType.SLOPED_POST));

	public static final RegistryObject<HardenedWoodBlock> HARDENED_WOOD = register("hardened_wood", () -> new HardenedWoodBlock(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.5f, 12.0f)));
	public static final RegistryObject<PlanterPotBlock> PLANTER_POT = register("planter_pot", () -> new PlanterPotBlock());

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

	public static Block[] colorizerBlocks() {
		return new Block[] { COLORIZER.get(), COLORIZER_CHAIR.get(), COLORIZER_TABLE.get(), COLORIZER_COUNTER.get(), COLORIZER_STOOL.get(), COLORIZER_SLOPE.get(), COLORIZER_SLOPED_ANGLE.get(), COLORIZER_SLOPED_INTERSECTION.get(), COLORIZER_SLOPED_POST.get(), COLORIZER_OBLIQUE_SLOPE.get(), COLORIZER_CORNER.get(), COLORIZER_SLANTED_CORNER.get(), COLORIZER_PYRAMID.get(), COLORIZER_FULL_PYRAMID.get() };
	}
}

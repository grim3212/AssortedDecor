package com.grim3212.assorted.decor.common.block;

import java.util.function.Function;
import java.util.function.Supplier;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.item.DecorItems;
import com.grim3212.assorted.decor.common.util.ColorizerUtil.SlopeType;

import net.minecraft.block.Block;
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
	public static final RegistryObject<ColorizerFenceBlock> COLORIZER_FENCE = register("colorizer_fence", () -> new ColorizerFenceBlock());
	public static final RegistryObject<ColorizerFenceGateBlock> COLORIZER_FENCE_GATE = register("colorizer_fence_gate", () -> new ColorizerFenceGateBlock());
	public static final RegistryObject<ColorizerWallBlock> COLORIZER_WALL = register("colorizer_wall", () -> new ColorizerWallBlock());
	public static final RegistryObject<ColorizerTrapDoorBlock> COLORIZER_TRAP_DOOR = register("colorizer_trap_door", () -> new ColorizerTrapDoorBlock());
	public static final RegistryObject<ColorizerDoorBlock> COLORIZER_DOOR = register("colorizer_door", () -> new ColorizerDoorBlock());
	public static final RegistryObject<ColorizerSlabBlock> COLORIZER_SLAB = register("colorizer_slab", () -> new ColorizerSlabBlock());
	public static final RegistryObject<ColorizerStairsBlock> COLORIZER_STAIRS = register("colorizer_stairs", () -> new ColorizerStairsBlock());
	public static final RegistryObject<ColorizerBlock> COLORIZER_LAMP_POST = register("colorizer_lamp_post", () -> new ColorizerLampPost());

	public static final RegistryObject<ColorizerBlock> COLORIZER_SLOPE = register("colorizer_slope", () -> new ColorizerSlopeBlock(SlopeType.SLOPE));
	public static final RegistryObject<ColorizerBlock> COLORIZER_SLOPED_ANGLE = register("colorizer_sloped_angle", () -> new ColorizerSlopeBlock(SlopeType.SLOPED_ANGLE));
	public static final RegistryObject<ColorizerBlock> COLORIZER_SLOPED_INTERSECTION = register("colorizer_sloped_intersection", () -> new ColorizerSlopeBlock(SlopeType.SLOPED_INTERSECTION));
	public static final RegistryObject<ColorizerBlock> COLORIZER_OBLIQUE_SLOPE = register("colorizer_oblique_slope", () -> new ColorizerSlopeBlock(SlopeType.OBLIQUE_SLOPE));
	public static final RegistryObject<ColorizerBlock> COLORIZER_CORNER = register("colorizer_corner", () -> new ColorizerSlopeBlock(SlopeType.CORNER));
	public static final RegistryObject<ColorizerBlock> COLORIZER_SLANTED_CORNER = register("colorizer_slanted_corner", () -> new ColorizerSlopeBlock(SlopeType.SLANTED_CORNER));
	public static final RegistryObject<ColorizerBlock> COLORIZER_PYRAMID = register("colorizer_pyramid", () -> new ColorizerSlopeSideBlock(SlopeType.PYRAMID));
	public static final RegistryObject<ColorizerBlock> COLORIZER_FULL_PYRAMID = register("colorizer_full_pyramid", () -> new ColorizerSlopeSideBlock(SlopeType.FULL_PYRAMID));
	public static final RegistryObject<ColorizerBlock> COLORIZER_SLOPED_POST = register("colorizer_sloped_post", () -> new ColorizerSlopeSideBlock(SlopeType.SLOPED_POST));

	public static final RegistryObject<ColorizerBlock> COLORIZER_CHIMNEY = register("colorizer_chimney", () -> new ColorizerChimneyBlock());
	public static final RegistryObject<ColorizerBlock> COLORIZER_FIREPLACE = register("colorizer_fireplace", () -> new ColorizerFireplaceBlock());
	public static final RegistryObject<ColorizerBlock> COLORIZER_FIRERING = register("colorizer_firering", () -> new ColorizerFireringBlock());
	public static final RegistryObject<ColorizerBlock> COLORIZER_FIREPIT = register("colorizer_firepit", () -> new ColorizerFirepitBlock(false));
	public static final RegistryObject<ColorizerBlock> COLORIZER_FIREPIT_COVERED = register("colorizer_firepit_covered", () -> new ColorizerFirepitBlock(true));
	public static final RegistryObject<ColorizerBlock> COLORIZER_STOVE = register("colorizer_stove", () -> new ColorizerStoveBlock());

	public static final RegistryObject<PlanterPotBlock> PLANTER_POT = register("planter_pot", () -> new PlanterPotBlock());

	private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup) {
		return register(name, sup, block -> item(block));
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
		return new Block[] { COLORIZER.get(), COLORIZER_CHAIR.get(), COLORIZER_TABLE.get(), COLORIZER_COUNTER.get(), COLORIZER_STOOL.get(), COLORIZER_FENCE.get(), COLORIZER_FENCE_GATE.get(), COLORIZER_WALL.get(), COLORIZER_TRAP_DOOR.get(), COLORIZER_DOOR.get(), COLORIZER_SLAB.get(), COLORIZER_STAIRS.get(), COLORIZER_LAMP_POST.get(), COLORIZER_SLOPE.get(), COLORIZER_SLOPED_ANGLE.get(), COLORIZER_SLOPED_INTERSECTION.get(), COLORIZER_SLOPED_POST.get(), COLORIZER_OBLIQUE_SLOPE.get(),
				COLORIZER_CORNER.get(), COLORIZER_SLANTED_CORNER.get(), COLORIZER_PYRAMID.get(), COLORIZER_FULL_PYRAMID.get(), COLORIZER_FIREPLACE.get(), COLORIZER_CHIMNEY.get(), COLORIZER_FIRERING.get(), COLORIZER_FIREPIT.get(), COLORIZER_FIREPIT_COVERED.get(), COLORIZER_STOVE.get() };
	}
}

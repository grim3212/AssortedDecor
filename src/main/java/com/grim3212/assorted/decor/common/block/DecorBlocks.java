package com.grim3212.assorted.decor.common.block;

import java.util.function.Function;
import java.util.function.Supplier;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerChairBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerChimneyBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerCounterBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerDoorBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerFenceBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerFenceGateBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerFirepitBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerFireplaceBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerFireringBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerLampPost;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerSlabBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerSlopeBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerSlopeSideBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerStairsBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerStoolBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerStoveBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerTableBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerTrapDoorBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerVerticalSlabBlock;
import com.grim3212.assorted.decor.common.block.colorizer.ColorizerWallBlock;
import com.grim3212.assorted.decor.common.item.DecorItems;
import com.grim3212.assorted.decor.common.util.ColorizerUtil.SlopeType;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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
	public static final RegistryObject<ColorizerVerticalSlabBlock> COLORIZER_VERTICAL_SLAB = register("colorizer_vertical_slab", () -> new ColorizerVerticalSlabBlock());
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

	public static final RegistryObject<NeonSignBlock> NEON_SIGN = registerNoItem("neon_sign", () -> new NeonSignStandingBlock());
	public static final RegistryObject<NeonSignBlock> NEON_SIGN_WALL = registerNoItem("neon_sign_wall", () -> new NeonSignWallBlock());

	public static final RegistryObject<IlluminationTubeBlock> ILLUMINATION_TUBE = register("illumination_tube", () -> new IlluminationTubeBlock(Block.Properties.of(Material.DECORATION).noCollission().instabreak().lightLevel(state -> 15).sound(SoundType.GLASS)));

	public static final RegistryObject<FluroBlock> FLURO_WHITE = register("fluro_white", () -> new FluroBlock(MaterialColor.SNOW));
	public static final RegistryObject<FluroBlock> FLURO_ORANGE = register("fluro_orange", () -> new FluroBlock(MaterialColor.COLOR_ORANGE));
	public static final RegistryObject<FluroBlock> FLURO_MAGENTA = register("fluro_magenta", () -> new FluroBlock(MaterialColor.COLOR_MAGENTA));
	public static final RegistryObject<FluroBlock> FLURO_LIGHT_BLUE = register("fluro_light_blue", () -> new FluroBlock(MaterialColor.COLOR_LIGHT_BLUE));
	public static final RegistryObject<FluroBlock> FLURO_YELLOW = register("fluro_yellow", () -> new FluroBlock(MaterialColor.COLOR_YELLOW));
	public static final RegistryObject<FluroBlock> FLURO_LIME = register("fluro_lime", () -> new FluroBlock(MaterialColor.COLOR_LIGHT_GREEN));
	public static final RegistryObject<FluroBlock> FLURO_PINK = register("fluro_pink", () -> new FluroBlock(MaterialColor.COLOR_PINK));
	public static final RegistryObject<FluroBlock> FLURO_GRAY = register("fluro_gray", () -> new FluroBlock(MaterialColor.COLOR_GRAY));
	public static final RegistryObject<FluroBlock> FLURO_LIGHT_GRAY = register("fluro_light_gray", () -> new FluroBlock(MaterialColor.COLOR_LIGHT_GRAY));
	public static final RegistryObject<FluroBlock> FLURO_CYAN = register("fluro_cyan", () -> new FluroBlock(MaterialColor.COLOR_CYAN));
	public static final RegistryObject<FluroBlock> FLURO_PURPLE = register("fluro_purple", () -> new FluroBlock(MaterialColor.COLOR_PURPLE));
	public static final RegistryObject<FluroBlock> FLURO_BLUE = register("fluro_blue", () -> new FluroBlock(MaterialColor.COLOR_BLUE));
	public static final RegistryObject<FluroBlock> FLURO_BROWN = register("fluro_brown", () -> new FluroBlock(MaterialColor.COLOR_BROWN));
	public static final RegistryObject<FluroBlock> FLURO_GREEN = register("fluro_green", () -> new FluroBlock(MaterialColor.COLOR_GREEN));
	public static final RegistryObject<FluroBlock> FLURO_RED = register("fluro_red", () -> new FluroBlock(MaterialColor.COLOR_RED));
	public static final RegistryObject<FluroBlock> FLURO_BLACK = register("fluro_black", () -> new FluroBlock(MaterialColor.COLOR_BLACK));

	public static final RegistryObject<DoorBlock> QUARTZ_DOOR = register("quartz_door", () -> new DoorBlock(Block.Properties.of(Material.METAL, MaterialColor.QUARTZ).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion()));

	public static final RegistryObject<CalendarBlock> CALENDAR = register("calendar", () -> new CalendarBlock(Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).noCollission().strength(1f)));
	public static final RegistryObject<WallClockBlock> WALL_CLOCK = register("wall_clock", () -> new WallClockBlock(Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1f)));

	public static Block[] fluroBlocks() {
		return new Block[] { FLURO_WHITE.get(), FLURO_ORANGE.get(), FLURO_MAGENTA.get(), FLURO_LIGHT_BLUE.get(), FLURO_YELLOW.get(), FLURO_LIME.get(), FLURO_PINK.get(), FLURO_GRAY.get(), FLURO_LIGHT_GRAY.get(), FLURO_CYAN.get(), FLURO_PURPLE.get(), FLURO_BLUE.get(), FLURO_BROWN.get(), FLURO_GREEN.get(), FLURO_RED.get(), FLURO_BLACK.get() };
	}

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
		return () -> new BlockItem(block.get(), new Item.Properties().tab(AssortedDecor.ASSORTED_DECOR_ITEM_GROUP));
	}

	public static Block[] colorizerBlocks() {
		return new Block[] { COLORIZER.get(), COLORIZER_CHAIR.get(), COLORIZER_TABLE.get(), COLORIZER_COUNTER.get(), COLORIZER_STOOL.get(), COLORIZER_FENCE.get(), COLORIZER_FENCE_GATE.get(), COLORIZER_WALL.get(), COLORIZER_TRAP_DOOR.get(), COLORIZER_DOOR.get(), COLORIZER_SLAB.get(), COLORIZER_VERTICAL_SLAB.get(), COLORIZER_STAIRS.get(), COLORIZER_LAMP_POST.get(), COLORIZER_SLOPE.get(), COLORIZER_SLOPED_ANGLE.get(), COLORIZER_SLOPED_INTERSECTION.get(), COLORIZER_SLOPED_POST.get(),
				COLORIZER_OBLIQUE_SLOPE.get(), COLORIZER_CORNER.get(), COLORIZER_SLANTED_CORNER.get(), COLORIZER_PYRAMID.get(), COLORIZER_FULL_PYRAMID.get(), COLORIZER_FIREPLACE.get(), COLORIZER_CHIMNEY.get(), COLORIZER_FIRERING.get(), COLORIZER_FIREPIT.get(), COLORIZER_FIREPIT_COVERED.get(), COLORIZER_STOVE.get() };
	}
}

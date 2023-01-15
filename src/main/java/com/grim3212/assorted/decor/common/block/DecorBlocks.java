package com.grim3212.assorted.decor.common.block;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.Maps;
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
import com.grim3212.assorted.decor.common.item.ColorChangingItem;
import com.grim3212.assorted.decor.common.item.DecorItems;
import com.grim3212.assorted.decor.common.util.ColorizerUtil.SlopeType;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
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
	public static final RegistryObject<IlluminationPlateBlock> ILLUMINATION_PLATE = register("illumination_plate", () -> new IlluminationPlateBlock(Block.Properties.of(Material.DECORATION).noCollission().strength(0.5F).lightLevel(state -> 15).sound(SoundType.GLASS)));

	public static final RegistryObject<FluroBlock> FLURO_WHITE = register("fluro_white", () -> new FluroBlock(DyeColor.WHITE));
	public static final RegistryObject<FluroBlock> FLURO_ORANGE = register("fluro_orange", () -> new FluroBlock(DyeColor.ORANGE));
	public static final RegistryObject<FluroBlock> FLURO_MAGENTA = register("fluro_magenta", () -> new FluroBlock(DyeColor.MAGENTA));
	public static final RegistryObject<FluroBlock> FLURO_LIGHT_BLUE = register("fluro_light_blue", () -> new FluroBlock(DyeColor.LIGHT_BLUE));
	public static final RegistryObject<FluroBlock> FLURO_YELLOW = register("fluro_yellow", () -> new FluroBlock(DyeColor.YELLOW));
	public static final RegistryObject<FluroBlock> FLURO_LIME = register("fluro_lime", () -> new FluroBlock(DyeColor.LIME));
	public static final RegistryObject<FluroBlock> FLURO_PINK = register("fluro_pink", () -> new FluroBlock(DyeColor.PINK));
	public static final RegistryObject<FluroBlock> FLURO_GRAY = register("fluro_gray", () -> new FluroBlock(DyeColor.GRAY));
	public static final RegistryObject<FluroBlock> FLURO_LIGHT_GRAY = register("fluro_light_gray", () -> new FluroBlock(DyeColor.LIGHT_GRAY));
	public static final RegistryObject<FluroBlock> FLURO_CYAN = register("fluro_cyan", () -> new FluroBlock(DyeColor.CYAN));
	public static final RegistryObject<FluroBlock> FLURO_PURPLE = register("fluro_purple", () -> new FluroBlock(DyeColor.PURPLE));
	public static final RegistryObject<FluroBlock> FLURO_BLUE = register("fluro_blue", () -> new FluroBlock(DyeColor.BLUE));
	public static final RegistryObject<FluroBlock> FLURO_BROWN = register("fluro_brown", () -> new FluroBlock(DyeColor.BROWN));
	public static final RegistryObject<FluroBlock> FLURO_GREEN = register("fluro_green", () -> new FluroBlock(DyeColor.GREEN));
	public static final RegistryObject<FluroBlock> FLURO_RED = register("fluro_red", () -> new FluroBlock(DyeColor.RED));
	public static final RegistryObject<FluroBlock> FLURO_BLACK = register("fluro_black", () -> new FluroBlock(DyeColor.BLACK));

	public static final RegistryObject<DoorBlock> QUARTZ_DOOR = register("quartz_door", () -> new DoorBlock(Block.Properties.of(Material.METAL, MaterialColor.QUARTZ).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion(), SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN));
	public static final RegistryObject<DoorBlock> GLASS_DOOR = register("glass_door", () -> new DoorBlock(Block.Properties.of(Material.GLASS, Blocks.GLASS.defaultMaterialColor()).strength(0.75F, 7.5F).sound(SoundType.GLASS).noOcclusion(), SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN));
	public static final RegistryObject<DoorBlock> STEEL_DOOR = register("steel_door", () -> new DoorBlock(Block.Properties.of(Material.METAL).strength(1.0F, 10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion(), SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN));
	public static final RegistryObject<DoorBlock> CHAIN_LINK_DOOR = register("chain_link_door", () -> new DoorBlock(Block.Properties.of(Material.DECORATION).strength(0.5F, 5.0F).sound(SoundType.METAL).noOcclusion(), SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN));
	public static final RegistryObject<IronBarsBlock> CHAIN_LINK_FENCE = register("chain_link_fence", () -> new IronBarsBlock(Block.Properties.of(Material.DECORATION).strength(0.5F, 5.0F).sound(SoundType.METAL).noOcclusion()));
	public static final RegistryObject<FountainBlock> FOUNTAIN = register("fountain", () -> new FountainBlock(Block.Properties.of(Material.STONE).strength(1.5F, 10.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> STONE_PATH = register("stone_path", () -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(0.5F, 10.0F).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> DECORATIVE_STONE = register("decorative_stone", () -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(0.5F, 10.0F).requiresCorrectToolForDrops()));

	public static final RegistryObject<CalendarBlock> CALENDAR = register("calendar", () -> new CalendarBlock(Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).noCollission().strength(1f)));
	public static final RegistryObject<WallClockBlock> WALL_CLOCK = register("wall_clock", () -> new WallClockBlock(Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1f)));

	public static final RegistryObject<ClayDecorationBlock> CLAY_DECORATION = register("clay_decoration", () -> new ClayDecorationBlock(Block.Properties.of(Material.DECORATION).sound(SoundType.STONE).noCollission().color(MaterialColor.COLOR_BROWN).instabreak()));
	public static final RegistryObject<BoneDecorationBlock> BONE_DECORATION = register("bone_decoration", () -> new BoneDecorationBlock(Block.Properties.of(Material.DECORATION).sound(SoundType.BONE_BLOCK).noCollission().color(MaterialColor.COLOR_LIGHT_GRAY).instabreak()));

	public static final RegistryObject<LanternBlock> PAPER_LANTERN = register("paper_lantern", () -> new LanternBlock(Block.Properties.of(Material.DECORATION).sound(SoundType.WOOL).noCollission().color(MaterialColor.COLOR_RED).strength(0.1F)));
	public static final RegistryObject<LanternBlock> BONE_LANTERN = register("bone_lantern", () -> new LanternBlock(Block.Properties.of(Material.DECORATION).sound(SoundType.BONE_BLOCK).noCollission().color(MaterialColor.COLOR_RED).strength(0.1F)));
	public static final RegistryObject<LanternBlock> IRON_LANTERN = register("iron_lantern", () -> new LanternBlock(Block.Properties.of(Material.METAL).sound(SoundType.METAL).noCollission().color(MaterialColor.COLOR_GRAY).strength(0.5F)));

	public static final RegistryObject<Block> SIDEWALK = register("sidewalk", () -> new Block(Block.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY).sound(SoundType.STONE).strength(1.0F, 15.0F).requiresCorrectToolForDrops().friction(0.4F)));
	public static final RegistryObject<CageBlock> CAGE = register("cage", () -> new CageBlock(Block.Properties.of(Material.METAL).sound(SoundType.METAL).strength(0.8F, 5.0F).requiresCorrectToolForDrops().noOcclusion().isValidSpawn(DecorBlocks::never).isRedstoneConductor(DecorBlocks::never).isSuffocating(DecorBlocks::never).isViewBlocking(DecorBlocks::never)));

	public static final RegistryObject<ColorChangingBlock> SIDING_VERTICAL = registerColorChanging("siding_vertical", () -> new ColorChangingBlock(Block.Properties.of(Material.METAL).sound(SoundType.STONE).strength(1.0F, 10.0F).requiresCorrectToolForDrops()));
	public static final RegistryObject<ColorChangingBlock> SIDING_HORIZONTAL = registerColorChanging("siding_horizontal", () -> new ColorChangingBlock(Block.Properties.of(Material.METAL).sound(SoundType.STONE).strength(1.0F, 10.0F).requiresCorrectToolForDrops()));

	public static final RegistryObject<RoadwayBlock> ROADWAY = register("roadway", () -> new RoadwayBlock(Block.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY).sound(SoundType.STONE).strength(1.0F, 15.0F).requiresCorrectToolForDrops()));
	public static final RegistryObject<RoadwayManholeBlock> ROADWAY_MANHOLE = register("roadway_manhole", () -> new RoadwayManholeBlock(Block.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY).sound(SoundType.METAL).strength(1.0F, 10.0F).requiresCorrectToolForDrops()));
	public static final RegistryObject<RoadwayLightBlock> ROADWAY_LIGHT = register("roadway_light", () -> new RoadwayLightBlock(Block.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY).sound(SoundType.STONE).strength(1.0F, 15.0F).requiresCorrectToolForDrops().lightLevel((b) -> b.getValue(RoadwayLightBlock.ACTIVE) ? 15 : 0)));

	public static final Map<DyeColor, RegistryObject<RoadwayColorBlock>> ROADWAY_COLORS = Maps.newHashMap();

	static {
		ROADWAY_COLORS.put(DyeColor.WHITE, register("roadway_white", () -> new RoadwayWhiteBlock(Block.Properties.of(Material.STONE, MaterialColor.SNOW).sound(SoundType.STONE).strength(1.0F, 15.0F).requiresCorrectToolForDrops())));
		Arrays.stream(DyeColor.values()).filter((c) -> c != DyeColor.WHITE).forEach((color) -> ROADWAY_COLORS.put(color, register("roadway_" + color.getName(), () -> new RoadwayColorBlock(color, Block.Properties.of(Material.STONE, color.getMaterialColor()).sound(SoundType.STONE).strength(1.0F, 15.0F).requiresCorrectToolForDrops()))));
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
		return () -> new BlockItem(block.get(), new Item.Properties());
	}

	private static <T extends Block> RegistryObject<T> registerColorChanging(String name, Supplier<? extends T> sup) {
		return register(name, sup, block -> colorChangingItem(block));
	}

	private static Supplier<BlockItem> colorChangingItem(final RegistryObject<? extends Block> block) {
		return () -> new ColorChangingItem(block.get(), new Item.Properties());
	}

	public static Block[] colorizerBlocks() {
		return new Block[] { COLORIZER.get(), COLORIZER_CHAIR.get(), COLORIZER_TABLE.get(), COLORIZER_COUNTER.get(), COLORIZER_STOOL.get(), COLORIZER_FENCE.get(), COLORIZER_FENCE_GATE.get(), COLORIZER_WALL.get(), COLORIZER_TRAP_DOOR.get(), COLORIZER_DOOR.get(), COLORIZER_SLAB.get(), COLORIZER_VERTICAL_SLAB.get(), COLORIZER_STAIRS.get(), COLORIZER_LAMP_POST.get(), COLORIZER_SLOPE.get(), COLORIZER_SLOPED_ANGLE.get(), COLORIZER_SLOPED_INTERSECTION.get(), COLORIZER_SLOPED_POST.get(),
				COLORIZER_OBLIQUE_SLOPE.get(), COLORIZER_CORNER.get(), COLORIZER_SLANTED_CORNER.get(), COLORIZER_PYRAMID.get(), COLORIZER_FULL_PYRAMID.get(), COLORIZER_FIREPLACE.get(), COLORIZER_CHIMNEY.get(), COLORIZER_FIRERING.get(), COLORIZER_FIREPIT.get(), COLORIZER_FIREPIT_COVERED.get(), COLORIZER_STOVE.get() };
	}

	private static boolean never(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> type) {
		return false;
	}

	private static boolean never(BlockState state, BlockGetter getter, BlockPos pos) {
		return false;
	}
}

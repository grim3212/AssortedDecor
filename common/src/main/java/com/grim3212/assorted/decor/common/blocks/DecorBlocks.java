package com.grim3212.assorted.decor.common.blocks;

import com.google.common.collect.Maps;
import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.api.colorizer.SlopeType;
import com.grim3212.assorted.decor.common.blocks.colorizer.*;
import com.grim3212.assorted.decor.common.items.ColorChangingItem;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class DecorBlocks {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, Constants.MOD_ID);
    // Blocks and their item forms get registered before other items
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID);

    public static final IRegistryObject<ColorizerBlock> COLORIZER = register("colorizer", props -> new ColorizerFullCubeBlock(colorizer(props).lightLevel(BlockState::getLightEmission)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_CHAIR = register("colorizer_chair", props -> new ColorizerChairBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_TABLE = register("colorizer_table", props -> new ColorizerTableBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_COUNTER = register("colorizer_counter", props -> new ColorizerCounterBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_STOOL = register("colorizer_stool", props -> new ColorizerStoolBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerFenceBlock> COLORIZER_FENCE = register("colorizer_fence", props -> new ColorizerFenceBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerFenceGateBlock> COLORIZER_FENCE_GATE = register("colorizer_fence_gate", props -> new ColorizerFenceGateBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerWallBlock> COLORIZER_WALL = register("colorizer_wall", props -> new ColorizerWallBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerTrapDoorBlock> COLORIZER_TRAP_DOOR = register("colorizer_trap_door", props -> new ColorizerTrapDoorBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerDoorBlock> COLORIZER_DOOR = register("colorizer_door", props -> new ColorizerDoorBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerSlabBlock> COLORIZER_SLAB = register("colorizer_slab", props -> new ColorizerSlabBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerVerticalSlabBlock> COLORIZER_VERTICAL_SLAB = register("colorizer_vertical_slab", props -> new ColorizerVerticalSlabBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerStairsBlock> COLORIZER_STAIRS = register("colorizer_stairs", props -> new ColorizerStairsBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_LAMP_POST = register("colorizer_lamp_post", props -> new ColorizerLampPost(colorizer(props)));

    public static final IRegistryObject<ColorizerBlock> COLORIZER_SLOPE = register("colorizer_slope", props -> new ColorizerSlopeBlock(SlopeType.SLOPE, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_SLOPED_ANGLE = register("colorizer_sloped_angle", props -> new ColorizerSlopeBlock(SlopeType.SLOPED_ANGLE, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_SLOPED_INTERSECTION = register("colorizer_sloped_intersection", props -> new ColorizerSlopeBlock(SlopeType.SLOPED_INTERSECTION, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_OBLIQUE_SLOPE = register("colorizer_oblique_slope", props -> new ColorizerSlopeBlock(SlopeType.OBLIQUE_SLOPE, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_CORNER = register("colorizer_corner", props -> new ColorizerSlopeBlock(SlopeType.CORNER, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_SLANTED_CORNER = register("colorizer_slanted_corner", props -> new ColorizerSlopeBlock(SlopeType.SLANTED_CORNER, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_PYRAMID = register("colorizer_pyramid", props -> new ColorizerSlopeSideBlock(SlopeType.PYRAMID, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_FULL_PYRAMID = register("colorizer_full_pyramid", props -> new ColorizerSlopeSideBlock(SlopeType.FULL_PYRAMID, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_SLOPED_POST = register("colorizer_sloped_post", props -> new ColorizerSlopeSideBlock(SlopeType.SLOPED_POST, colorizer(props)));

    public static final IRegistryObject<ColorizerBlock> COLORIZER_CHIMNEY = register("colorizer_chimney", props -> new ColorizerChimneyBlock(colorizer(props).lightLevel(BlockState::getLightEmission)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_FIREPLACE = register("colorizer_fireplace", props -> new ColorizerFireplaceBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_FIRERING = register("colorizer_firering", props -> new ColorizerFireringBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_FIREPIT = register("colorizer_firepit", props -> new ColorizerFirepitBlock(false, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_FIREPIT_COVERED = register("colorizer_firepit_covered", props -> new ColorizerFirepitBlock(true, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_STOVE = register("colorizer_stove", props -> new ColorizerStoveBlock(colorizer(props)));

    public static final IRegistryObject<PlanterPotBlock> PLANTER_POT = register("planter_pot", props -> new PlanterPotBlock(props.mapColor(MapColor.CLAY).sound(SoundType.GRAVEL).randomTicks().strength(0.5f, 10f).dynamicShape().noOcclusion()));

    public static final IRegistryObject<NeonSignBlock> NEON_SIGN = registerNoItem("neon_sign", props -> new NeonSignStandingBlock(props.mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).strength(10f).noCollision()));
    public static final IRegistryObject<NeonSignBlock> NEON_SIGN_WALL = registerNoItem("neon_sign_wall", props -> new NeonSignWallBlock(props.mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).strength(10f).noCollision().overrideLootTable(NEON_SIGN.get().getLootTable()).overrideDescription(NEON_SIGN.get().getDescriptionId())));

    public static final IRegistryObject<IlluminationTubeBlock> ILLUMINATION_TUBE = register("illumination_tube", props -> new IlluminationTubeBlock(props.pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, getter, pos) -> false).noCollision().instabreak().lightLevel(state -> 15).sound(SoundType.GLASS)));
    public static final IRegistryObject<IlluminationPlateBlock> ILLUMINATION_PLATE = register("illumination_plate", props -> new IlluminationPlateBlock(props.pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, getter, pos) -> false).noCollision().strength(0.5F).lightLevel(state -> 15).sound(SoundType.GLASS)));

    public static final IRegistryObject<FluroBlock> FLURO_WHITE = register("fluro_white", props -> new FluroBlock(DyeColor.WHITE, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_ORANGE = register("fluro_orange", props -> new FluroBlock(DyeColor.ORANGE, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_MAGENTA = register("fluro_magenta", props -> new FluroBlock(DyeColor.MAGENTA, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_LIGHT_BLUE = register("fluro_light_blue", props -> new FluroBlock(DyeColor.LIGHT_BLUE, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_YELLOW = register("fluro_yellow", props -> new FluroBlock(DyeColor.YELLOW, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_LIME = register("fluro_lime", props -> new FluroBlock(DyeColor.LIME, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_PINK = register("fluro_pink", props -> new FluroBlock(DyeColor.PINK, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_GRAY = register("fluro_gray", props -> new FluroBlock(DyeColor.GRAY, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_LIGHT_GRAY = register("fluro_light_gray", props -> new FluroBlock(DyeColor.LIGHT_GRAY, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_CYAN = register("fluro_cyan", props -> new FluroBlock(DyeColor.CYAN, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_PURPLE = register("fluro_purple", props -> new FluroBlock(DyeColor.PURPLE, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_BLUE = register("fluro_blue", props -> new FluroBlock(DyeColor.BLUE, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_BROWN = register("fluro_brown", props -> new FluroBlock(DyeColor.BROWN, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_GREEN = register("fluro_green", props -> new FluroBlock(DyeColor.GREEN, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_RED = register("fluro_red", props -> new FluroBlock(DyeColor.RED, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_BLACK = register("fluro_black", props -> new FluroBlock(DyeColor.BLACK, fluro(props)));

    public static final IRegistryObject<DecorDoorBlock> QUARTZ_DOOR = register("quartz_door", props -> new DecorDoorBlock(props.mapColor(MapColor.QUARTZ).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion()));
    public static final IRegistryObject<DecorDoorBlock> GLASS_DOOR = register("glass_door", props -> new DecorDoorBlock(props.mapColor(Blocks.GLASS.defaultMapColor()).instrument(NoteBlockInstrument.HAT).strength(0.75F, 7.5F).sound(SoundType.GLASS).noOcclusion()));
    public static final IRegistryObject<DecorDoorBlock> STEEL_DOOR = register("steel_door", props -> new DecorDoorBlock(props.mapColor(MapColor.METAL).strength(1.0F, 10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion()));
    public static final IRegistryObject<DecorDoorBlock> CHAIN_LINK_DOOR = register("chain_link_door", props -> new DecorDoorBlock(props.mapColor(MapColor.METAL).strength(0.5F, 5.0F).sound(SoundType.METAL).noOcclusion()));
    public static final IRegistryObject<DecorBarsBlock> CHAIN_LINK_FENCE = register("chain_link_fence", props -> new DecorBarsBlock(props.mapColor(MapColor.METAL).strength(0.5F, 5.0F).sound(SoundType.METAL).noOcclusion()));
    public static final IRegistryObject<FountainBlock> FOUNTAIN = register("fountain", props -> new FountainBlock(props.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 10.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()));
    public static final IRegistryObject<Block> STONE_PATH = register("stone_path", props -> new Block(props.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(0.5F, 10.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<Block> DECORATIVE_STONE = register("decorative_stone", props -> new Block(props.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(0.5F, 10.0F).requiresCorrectToolForDrops()));

    public static final IRegistryObject<CalendarBlock> CALENDAR = register("calendar", props -> new CalendarBlock(props.mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).noCollision().strength(1f)));
    public static final IRegistryObject<WallClockBlock> WALL_CLOCK = register("wall_clock", props -> new WallClockBlock(props.mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).strength(1f)));

    public static final IRegistryObject<ClayDecorationBlock> CLAY_DECORATION = register("clay_decoration", props -> new ClayDecorationBlock(props.mapColor(MapColor.COLOR_BROWN).sound(SoundType.STONE).noCollision().instabreak()));
    public static final IRegistryObject<BoneDecorationBlock> BONE_DECORATION = register("bone_decoration", props -> new BoneDecorationBlock(props.mapColor(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.BONE_BLOCK).noCollision().instabreak()));

    public static final IRegistryObject<LanternBlock> PAPER_LANTERN = register("paper_lantern", props -> new LanternBlock(props.mapColor(MapColor.COLOR_RED).sound(SoundType.WOOL).noCollision().strength(0.1F)));
    public static final IRegistryObject<LanternBlock> BONE_LANTERN = register("bone_lantern", props -> new LanternBlock(props.mapColor(MapColor.COLOR_RED).sound(SoundType.BONE_BLOCK).noCollision().strength(0.1F)));
    public static final IRegistryObject<LanternBlock> IRON_LANTERN = register("iron_lantern", props -> new LanternBlock(props.mapColor(MapColor.COLOR_GRAY).sound(SoundType.METAL).noCollision().strength(0.5F)));

    /**
     * What makes the sidewalk the quicker surface. Not {@code friction}: below vanilla's 0.6
     * {@code LivingEntity#getFrictionInfluencedSpeed} no longer pays the acceleration back, so a low
     * friction only shortens the momentum carried between ticks. The factor multiplies horizontal
     * velocity every tick, settling a walk at {@code s / (1 - 0.546 * s)} times the base speed.
     */
    public static final float SIDEWALK_SPEED_FACTOR = 1.35F;

    public static final IRegistryObject<Block> SIDEWALK = register("sidewalk", props -> new Block(props.mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(1.0F, 15.0F).requiresCorrectToolForDrops().speedFactor(SIDEWALK_SPEED_FACTOR)));
    public static final IRegistryObject<CageBlock> CAGE = register("cage", props -> new CageBlock(props.mapColor(MapColor.METAL).sound(SoundType.METAL).strength(0.8F, 5.0F).requiresCorrectToolForDrops().noOcclusion().isValidSpawn(DecorBlocks::never).isRedstoneConductor(DecorBlocks::never).isSuffocating(DecorBlocks::never).isViewBlocking(DecorBlocks::never)));

    public static final IRegistryObject<ColorChangingBlock> SIDING_VERTICAL = registerColorChanging("siding_vertical", props -> new ColorChangingBlock(props.mapColor(MapColor.METAL).sound(SoundType.STONE).strength(1.0F, 10.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<ColorChangingBlock> SIDING_HORIZONTAL = registerColorChanging("siding_horizontal", props -> new ColorChangingBlock(props.mapColor(MapColor.METAL).sound(SoundType.STONE).strength(1.0F, 10.0F).requiresCorrectToolForDrops()));

    public static final IRegistryObject<RoadwayBlock> ROADWAY = register("roadway", props -> new RoadwayBlock(props.mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(1.0F, 15.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<RoadwayManholeBlock> ROADWAY_MANHOLE = register("roadway_manhole", props -> new RoadwayManholeBlock(props.mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(1.0F, 10.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<RoadwayLightBlock> ROADWAY_LIGHT = register("roadway_light", props -> new RoadwayLightBlock(props.mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(1.0F, 15.0F).requiresCorrectToolForDrops().lightLevel((b) -> b.getValue(RoadwayLightBlock.ACTIVE) ? 15 : 0)));

    public static final Map<DyeColor, IRegistryObject<RoadwayColorBlock>> ROADWAY_COLORS = Maps.newEnumMap(DyeColor.class);

    static {
        ROADWAY_COLORS.put(DyeColor.WHITE, register("roadway_white", props -> new RoadwayWhiteBlock(props.mapColor(MapColor.SNOW).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(1.0F, 15.0F).requiresCorrectToolForDrops())));
        Arrays.stream(DyeColor.values()).filter((c) -> c != DyeColor.WHITE).forEach((color) -> ROADWAY_COLORS.put(color, register("roadway_" + color.getName(), props -> new RoadwayColorBlock(color, props.mapColor(color.getMapColor()).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(1.0F, 15.0F).requiresCorrectToolForDrops()))));
    }

    /**
     * Every colorizer shares the same base properties, they only differ in the shape they take
     */
    private static BlockBehaviour.Properties colorizer(BlockBehaviour.Properties props) {
        return props.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5f, 12.0f).sound(SoundType.STONE).dynamicShape().noOcclusion();
    }

    private static BlockBehaviour.Properties fluro(BlockBehaviour.Properties props) {
        return props.instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS).strength(0.2F, 1.0F).lightLevel(state -> 15);
    }

    private static <T extends Block> IRegistryObject<T> register(String name, Function<BlockBehaviour.Properties, ? extends T> factory) {
        return register(name, factory, block -> item(name, block));
    }

    private static <T extends Block> IRegistryObject<T> register(String name, Function<BlockBehaviour.Properties, ? extends T> factory, Function<IRegistryObject<T>, Supplier<? extends Item>> itemCreator) {
        IRegistryObject<T> ret = registerNoItem(name, factory);
        ITEMS.register(name, itemCreator.apply(ret));
        return ret;
    }

    private static <T extends Block> IRegistryObject<T> registerNoItem(String name, Function<BlockBehaviour.Properties, ? extends T> factory) {
        // Since 1.21.2 every block has to know its own id before it is constructed, so the
        // properties are built here where the registration name is known.
        final ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return BLOCKS.register(name, () -> factory.apply(BlockBehaviour.Properties.of().setId(key)));
    }

    private static Supplier<BlockItem> item(final String name, final IRegistryObject<? extends Block> block) {
        final ResourceKey<Item> key = itemKey(name);
        return () -> new BlockItem(block.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key));
    }

    private static <T extends Block> IRegistryObject<T> registerColorChanging(String name, Function<BlockBehaviour.Properties, ? extends T> factory) {
        return register(name, factory, block -> colorChangingItem(name, block));
    }

    private static Supplier<BlockItem> colorChangingItem(final String name, final IRegistryObject<? extends Block> block) {
        final ResourceKey<Item> key = itemKey(name);
        return () -> new ColorChangingItem(block.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key));
    }

    private static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
    }

    public static List<IRegistryObject<? extends Block>> colorizerBlocks() {
        return Arrays.asList(COLORIZER, COLORIZER_CHAIR, COLORIZER_TABLE, COLORIZER_COUNTER, COLORIZER_STOOL, COLORIZER_FENCE, COLORIZER_FENCE_GATE, COLORIZER_WALL, COLORIZER_TRAP_DOOR, COLORIZER_DOOR, COLORIZER_SLAB, COLORIZER_VERTICAL_SLAB, COLORIZER_STAIRS, COLORIZER_LAMP_POST, COLORIZER_SLOPE, COLORIZER_SLOPED_ANGLE, COLORIZER_SLOPED_INTERSECTION, COLORIZER_SLOPED_POST,
                COLORIZER_OBLIQUE_SLOPE, COLORIZER_CORNER, COLORIZER_SLANTED_CORNER, COLORIZER_PYRAMID, COLORIZER_FULL_PYRAMID, COLORIZER_FIREPLACE, COLORIZER_CHIMNEY, COLORIZER_FIRERING, COLORIZER_FIREPIT, COLORIZER_FIREPIT_COVERED, COLORIZER_STOVE
        );
    }

    private static boolean never(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> type) {
        return false;
    }

    private static boolean never(BlockState state, BlockGetter getter, BlockPos pos) {
        return false;
    }

    public static void init() {
    }
}

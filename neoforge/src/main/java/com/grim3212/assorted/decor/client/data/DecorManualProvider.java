package com.grim3212.assorted.decor.client.data;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This mod's section of the instruction manual. The coloured families are read from the same maps
 * and lists the blocks are registered from.
 */
public class DecorManualProvider extends LibManualProvider {

    public DecorManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addChapters() {
        this.section(25, DecorBlocks.COLORIZER.get());

        this.addColorizer();
        this.addFurniture();
        this.addFires();
        this.addLights();
        this.addHanging();
        this.addRoads();
        this.addDecorations();
        this.addDoors();
    }

    private void addColorizer() {
        Block[] shapes = {DecorBlocks.COLORIZER_SLAB.get(), DecorBlocks.COLORIZER_VERTICAL_SLAB.get(),
                DecorBlocks.COLORIZER_STAIRS.get(), DecorBlocks.COLORIZER_WALL.get(),
                DecorBlocks.COLORIZER_FENCE.get(), DecorBlocks.COLORIZER_FENCE_GATE.get()};
        Block[] slopes = {DecorBlocks.COLORIZER_SLOPE.get(), DecorBlocks.COLORIZER_OBLIQUE_SLOPE.get(),
                DecorBlocks.COLORIZER_SLANTED_CORNER.get(), DecorBlocks.COLORIZER_SLOPED_ANGLE.get(),
                DecorBlocks.COLORIZER_SLOPED_INTERSECTION.get(), DecorBlocks.COLORIZER_SLOPED_POST.get(),
                DecorBlocks.COLORIZER_CORNER.get()};
        Block[] pyramids = {DecorBlocks.COLORIZER_PYRAMID.get(), DecorBlocks.COLORIZER_FULL_PYRAMID.get()};
        Block[] doors = {DecorBlocks.COLORIZER_DOOR.get(), DecorBlocks.COLORIZER_TRAP_DOOR.get()};

        ChapterBuilder colorizer = this.chapter("colorizer");
        colorizer.recipes("colorizer", "colorizer").opens(DecorBlocks.COLORIZER.get());
        colorizer.recipes("brush", "colorizer_brush").opens(DecorItems.COLORIZER_BRUSH.get());
        colorizer.recipes("shapes", "colorizer_slab", "colorizer_vertical_slab", "colorizer_stairs",
                "colorizer_wall", "colorizer_fence", "colorizer_fence_gate").every(50).opens(shapes);
        colorizer.recipes("slopes", "colorizer_slope", "colorizer_oblique_slope", "colorizer_slanted_corner",
                "colorizer_sloped_angle", "colorizer_sloped_intersection", "colorizer_sloped_post",
                "colorizer_corner").every(50).opens(slopes);
        colorizer.recipes("pyramids", "colorizer_pyramid", "colorizer_full_pyramid").every(50).opens(pyramids);
        colorizer.recipes("doors", "colorizer_door", "colorizer_trap_door").every(50).opens(doors);
    }

    private void addFurniture() {
        Block[] tables = {DecorBlocks.COLORIZER_TABLE.get(), DecorBlocks.COLORIZER_COUNTER.get()};
        Block[] seats = {DecorBlocks.COLORIZER_CHAIR.get(), DecorBlocks.COLORIZER_STOOL.get()};

        ChapterBuilder furniture = this.chapter("furniture");
        furniture.recipes("tables", "colorizer_table", "colorizer_counter").every(50).opens(tables);
        furniture.recipes("seats", "colorizer_chair", "colorizer_stool").every(50).opens(seats);
        furniture.recipes("lamp_post", "colorizer_lamp_post").opens(DecorBlocks.COLORIZER_LAMP_POST.get());
    }

    private void addFires() {
        Block[] firepits = {DecorBlocks.COLORIZER_FIREPIT.get(), DecorBlocks.COLORIZER_FIREPIT_COVERED.get()};

        ChapterBuilder fires = this.chapter("fires");
        fires.recipes("fireplace", "colorizer_fireplace").opens(DecorBlocks.COLORIZER_FIREPLACE.get());
        fires.recipes("stove", "colorizer_stove").opens(DecorBlocks.COLORIZER_STOVE.get());
        fires.recipes("firepit", "colorizer_firepit", "colorizer_firepit_covered").every(50).opens(firepits);
        fires.recipes("firering", "colorizer_firering").opens(DecorBlocks.COLORIZER_FIRERING.get());
        fires.recipes("chimney", "colorizer_chimney").opens(DecorBlocks.COLORIZER_CHIMNEY.get());
    }

    private void addLights() {
        Block[] fluro = {DecorBlocks.FLURO_WHITE.get(), DecorBlocks.FLURO_ORANGE.get(), DecorBlocks.FLURO_MAGENTA.get(),
                DecorBlocks.FLURO_LIGHT_BLUE.get(), DecorBlocks.FLURO_YELLOW.get(), DecorBlocks.FLURO_LIME.get(),
                DecorBlocks.FLURO_PINK.get(), DecorBlocks.FLURO_GRAY.get(), DecorBlocks.FLURO_LIGHT_GRAY.get(),
                DecorBlocks.FLURO_CYAN.get(), DecorBlocks.FLURO_PURPLE.get(), DecorBlocks.FLURO_BLUE.get(),
                DecorBlocks.FLURO_BROWN.get(), DecorBlocks.FLURO_GREEN.get(), DecorBlocks.FLURO_RED.get(),
                DecorBlocks.FLURO_BLACK.get()};
        Block[] illumination = {DecorBlocks.ILLUMINATION_TUBE.get(), DecorBlocks.ILLUMINATION_PLATE.get()};
        Block[] lanterns = {DecorBlocks.BONE_LANTERN.get(), DecorBlocks.IRON_LANTERN.get(),
                DecorBlocks.PAPER_LANTERN.get()};

        ChapterBuilder lights = this.chapter("lights");
        lights.recipes("fluro", "fluro_white", "fluro_orange", "fluro_magenta", "fluro_light_blue", "fluro_yellow", "fluro_lime", "fluro_pink", "fluro_gray", "fluro_light_gray", "fluro_cyan", "fluro_purple", "fluro_blue", "fluro_brown", "fluro_green", "fluro_red", "fluro_black").every(30).opens(fluro);
        lights.recipes("illumination", "illumination_tube", "illumination_plate").every(50).opens(illumination);
        lights.recipes("lanterns", "bone_lantern", "iron_lantern", "paper_lantern").every(50).opens(lanterns);
    }

    private void addHanging() {
        Item[] frames = {DecorItems.WOOD_FRAME.get(), DecorItems.IRON_FRAME.get()};

        ChapterBuilder hanging = this.chapter("hanging");
        hanging.recipes("wallpaper", "wallpaper").opens(DecorItems.WALLPAPER.get());
        hanging.recipes("frames", "wood_frame", "iron_frame").every(50).opens(frames);
        // New page rather than replacing the recipe: the shot shows what dyeing them looks like.
        hanging.image("frame_info", picture("frames"), 108, 104);
        hanging.recipes("calendar", "calendar").opens(DecorBlocks.CALENDAR.get());
        hanging.recipes("clock", "wall_clock").opens(DecorBlocks.WALL_CLOCK.get());
        // Both sign blocks share one item, whose id is the standing block's, so listing the blocks
        // covers it.
        hanging.recipes("neon_sign", "neon_sign")
                .opens(DecorBlocks.NEON_SIGN.get(), DecorBlocks.NEON_SIGN_WALL.get());
    }

    private void addRoads() {
        List<Block> roadway = new ArrayList<>(List.of(DecorBlocks.ROADWAY.get(), DecorBlocks.ROADWAY_LIGHT.get(),
                DecorBlocks.ROADWAY_MANHOLE.get()));
        roadway.addAll(values(DecorBlocks.ROADWAY_COLORS));
        Block[] paths = {DecorBlocks.SIDEWALK.get(), DecorBlocks.STONE_PATH.get()};

        List<Item> rollers = new ArrayList<>();
        rollers.add(DecorItems.PAINT_ROLLER.get());
        rollers.addAll(values(DecorItems.PAINT_ROLLER_COLORS));

        ChapterBuilder roads = this.chapter("roads");
        roads.recipes("roadway", "roadway", "roadway_light", "roadway_manhole").every(50)
                .opens(roadway.toArray(Block[]::new));
        roads.recipes("asphalt", "asphalt").opens(DecorItems.ASPHALT.get(), DecorItems.TARBALL.get());
        // The stone path comes off the stonecutter rather than a bench.
        roads.recipes("sidewalk", "sidewalk", "stone_path_stonecutting").every(50).opens(paths);
        roads.recipes("rollers", "paint_roller", "white_wool_paint_roll", "white_concrete_paint_roll").every(60)
                .opens(rollers.toArray(Item[]::new));
    }

    private void addDecorations() {
        Block[] siding = {DecorBlocks.SIDING_HORIZONTAL.get(), DecorBlocks.SIDING_VERTICAL.get()};

        ChapterBuilder decorations = this.chapter("decorations");
        decorations.recipes("clay", "unfired_clay_decoration", "clay_decoration",
                        "unfired_planter_pot", "planter_pot").every(50)
                .opens(DecorBlocks.CLAY_DECORATION.get(), DecorBlocks.PLANTER_POT.get())
                .opens(DecorItems.UNFIRED_CLAY_DECORATION.get(), DecorItems.UNFIRED_PLANTER_POT.get());
        decorations.recipes("bone", "bone_decoration").opens(DecorBlocks.BONE_DECORATION.get());
        decorations.recipes("stone", "decorative_path_stonecutting", "stone_path_stonecutting").every(60)
                .opens(DecorBlocks.DECORATIVE_STONE.get());
        decorations.recipes("siding", "siding_horizontal_white", "siding_vertical_white").every(60).opens(siding);
        decorations.recipes("fountain", "fountain").opens(DecorBlocks.FOUNTAIN.get());
        decorations.recipes("cage", "cage").opens(DecorBlocks.CAGE.get());
    }

    private void addDoors() {
        Block[] doors = {DecorBlocks.GLASS_DOOR.get(), DecorBlocks.QUARTZ_DOOR.get(), DecorBlocks.STEEL_DOOR.get()};

        ChapterBuilder chapter = this.chapter("doors");
        chapter.recipes("chain_link", "chain_link", "chain_link_fence", "chain_link_door").every(60)
                .opens(DecorBlocks.CHAIN_LINK_DOOR.get(), DecorBlocks.CHAIN_LINK_FENCE.get())
                .opens(DecorItems.CHAIN_LINK.get());
        chapter.recipes("doors", "glass_door", "quartz_door", "steel_door").every(50).opens(doors);
    }

    private static <T> List<T> values(Map<DyeColor, ? extends IRegistryObject<? extends T>> registered) {
        return registered.values().stream().map(IRegistryObject::get).map(t -> (T) t).toList();
    }

    /** The screenshots under {@code textures/gui/manual}, sized to leave room for the text below. */
    private static Identifier picture(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/manual/" + name + ".png");
    }
}

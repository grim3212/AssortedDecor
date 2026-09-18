package com.grim3212.assorted.decor.client.data;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.crafting.DecorConditions;
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
        this.section(60, DecorBlocks.COLORIZER.get());

        this.addColorizer();
        this.addFurniture();
        this.addFires();
        this.addLights();
        this.addHanging();
        this.addRoads();
        this.addDecorations();
        this.addDoors();
        this.addGates();
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

        ChapterBuilder colorizer = this.chapter("colorizer").whenPartEnabled(DecorConditions.Parts.COLORIZER);
        colorizer.recipes("colorizer", DecorBlocks.COLORIZER.get()).opens(DecorBlocks.COLORIZER.get());
        colorizer.recipes("brush", DecorItems.COLORIZER_BRUSH.get()).opens(DecorItems.COLORIZER_BRUSH.get());
        colorizer.recipes("shapes", DecorBlocks.COLORIZER_SLAB.get(), DecorBlocks.COLORIZER_VERTICAL_SLAB.get(), DecorBlocks.COLORIZER_STAIRS.get(), DecorBlocks.COLORIZER_WALL.get(), DecorBlocks.COLORIZER_FENCE.get(), DecorBlocks.COLORIZER_FENCE_GATE.get()).every(50).opens(shapes);
        colorizer.recipes("slopes", DecorBlocks.COLORIZER_SLOPE.get(), DecorBlocks.COLORIZER_OBLIQUE_SLOPE.get(), DecorBlocks.COLORIZER_SLANTED_CORNER.get(), DecorBlocks.COLORIZER_SLOPED_ANGLE.get(), DecorBlocks.COLORIZER_SLOPED_INTERSECTION.get(), DecorBlocks.COLORIZER_SLOPED_POST.get(), DecorBlocks.COLORIZER_CORNER.get()).every(50).opens(slopes);
        colorizer.recipes("pyramids", DecorBlocks.COLORIZER_PYRAMID.get(), DecorBlocks.COLORIZER_FULL_PYRAMID.get()).every(50).opens(pyramids);
        colorizer.recipes("doors", DecorBlocks.COLORIZER_DOOR.get(), DecorBlocks.COLORIZER_TRAP_DOOR.get()).every(50).opens(doors);
    }

    private void addFurniture() {
        Block[] tables = {DecorBlocks.COLORIZER_TABLE.get(), DecorBlocks.COLORIZER_COUNTER.get()};
        Block[] seats = {DecorBlocks.COLORIZER_CHAIR.get(), DecorBlocks.COLORIZER_STOOL.get()};

        ChapterBuilder furniture = this.chapter("furniture").whenPartEnabled(DecorConditions.Parts.COLORIZER);
        furniture.recipes("tables", DecorBlocks.COLORIZER_TABLE.get(), DecorBlocks.COLORIZER_COUNTER.get()).every(50).opens(tables);
        furniture.recipes("seats", DecorBlocks.COLORIZER_CHAIR.get(), DecorBlocks.COLORIZER_STOOL.get()).every(50).opens(seats);
        furniture.recipes("lamp_post", DecorBlocks.COLORIZER_LAMP_POST.get()).opens(DecorBlocks.COLORIZER_LAMP_POST.get());
    }

    private void addFires() {
        Block[] firepits = {DecorBlocks.COLORIZER_FIREPIT.get(), DecorBlocks.COLORIZER_FIREPIT_COVERED.get()};

        ChapterBuilder fires = this.chapter("fires").whenPartEnabled(DecorConditions.Parts.COLORIZER);
        fires.recipes("fireplace", DecorBlocks.COLORIZER_FIREPLACE.get()).opens(DecorBlocks.COLORIZER_FIREPLACE.get());
        fires.recipes("stove", DecorBlocks.COLORIZER_STOVE.get()).opens(DecorBlocks.COLORIZER_STOVE.get());
        fires.recipes("firepit", DecorBlocks.COLORIZER_FIREPIT.get(), DecorBlocks.COLORIZER_FIREPIT_COVERED.get()).every(50).opens(firepits);
        fires.recipes("firering", DecorBlocks.COLORIZER_FIRERING.get()).opens(DecorBlocks.COLORIZER_FIRERING.get());
        fires.recipes("chimney", DecorBlocks.COLORIZER_CHIMNEY.get()).opens(DecorBlocks.COLORIZER_CHIMNEY.get());
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
        lights.recipes("fluro", DecorBlocks.FLURO_WHITE.get(), DecorBlocks.FLURO_ORANGE.get(), DecorBlocks.FLURO_MAGENTA.get(), DecorBlocks.FLURO_LIGHT_BLUE.get(), DecorBlocks.FLURO_YELLOW.get(), DecorBlocks.FLURO_LIME.get(), DecorBlocks.FLURO_PINK.get(), DecorBlocks.FLURO_GRAY.get(), DecorBlocks.FLURO_LIGHT_GRAY.get(), DecorBlocks.FLURO_CYAN.get(), DecorBlocks.FLURO_PURPLE.get(), DecorBlocks.FLURO_BLUE.get(), DecorBlocks.FLURO_BROWN.get(), DecorBlocks.FLURO_GREEN.get(), DecorBlocks.FLURO_RED.get(), DecorBlocks.FLURO_BLACK.get()).every(30).opens(fluro)
                .whenPartEnabled(DecorConditions.Parts.FLURO);
        lights.recipes("illumination", DecorBlocks.ILLUMINATION_TUBE.get(), DecorBlocks.ILLUMINATION_PLATE.get()).every(50).opens(illumination)
                .whenPartEnabled(DecorConditions.Parts.FLURO);
        lights.recipes("lanterns", DecorBlocks.BONE_LANTERN.get(), DecorBlocks.IRON_LANTERN.get(), DecorBlocks.PAPER_LANTERN.get()).every(50).opens(lanterns)
                .whenPartEnabled(DecorConditions.Parts.DECORATIONS);
    }

    private void addHanging() {
        Item[] frames = {DecorItems.WOOD_FRAME.get(), DecorItems.IRON_FRAME.get()};

        ChapterBuilder hanging = this.chapter("hanging");
        hanging.recipes("wallpaper", DecorItems.WALLPAPER.get()).opens(DecorItems.WALLPAPER.get())
                .whenPartEnabled(DecorConditions.Parts.HANGEABLES);
        hanging.recipes("frames", DecorItems.WOOD_FRAME.get(), DecorItems.IRON_FRAME.get()).every(50).opens(frames)
                .whenPartEnabled(DecorConditions.Parts.HANGEABLES);
        // New page rather than replacing the recipe: the shot shows what dyeing them looks like.
        hanging.image("frame_info", picture("frames"), 108, 104).whenPartEnabled(DecorConditions.Parts.HANGEABLES);
        hanging.recipes("calendar", DecorBlocks.CALENDAR.get()).opens(DecorBlocks.CALENDAR.get())
                .whenPartEnabled(DecorConditions.Parts.HANGEABLES);
        hanging.recipes("clock", DecorBlocks.WALL_CLOCK.get()).opens(DecorBlocks.WALL_CLOCK.get())
                .whenPartEnabled(DecorConditions.Parts.HANGEABLES);
        // Both sign blocks share one item, whose id is the standing block's, so listing the blocks
        // covers it.
        hanging.recipes("neon_sign", DecorBlocks.NEON_SIGN.get())
                .opens(DecorBlocks.NEON_SIGN.get(), DecorBlocks.NEON_SIGN_WALL.get())
                .whenPartEnabled(DecorConditions.Parts.NEON_SIGN);
    }

    private void addRoads() {
        List<Block> roadway = new ArrayList<>(List.of(DecorBlocks.ROADWAY.get(), DecorBlocks.ROADWAY_LIGHT.get(),
                DecorBlocks.ROADWAY_MANHOLE.get()));
        roadway.addAll(values(DecorBlocks.ROADWAY_COLORS));

        List<Item> rollers = new ArrayList<>();
        rollers.add(DecorItems.PAINT_ROLLER.get());
        rollers.addAll(values(DecorItems.PAINT_ROLLER_COLORS));

        ChapterBuilder roads = this.chapter("roads");
        roads.recipes("roadway", DecorBlocks.ROADWAY.get(), DecorBlocks.ROADWAY_LIGHT.get(), DecorBlocks.ROADWAY_MANHOLE.get()).every(50)
                .opens(roadway.toArray(Block[]::new))
                .whenPartEnabled(DecorConditions.Parts.ROADWAYS);
        roads.recipes("asphalt", DecorItems.ASPHALT.get()).opens(DecorItems.ASPHALT.get(), DecorItems.TARBALL.get())
                .whenPartEnabled(DecorConditions.Parts.ROADWAYS);
        roads.recipes("sidewalk", DecorBlocks.SIDEWALK.get()).whenPartEnabled(DecorConditions.Parts.ROADWAYS)
                .opens(DecorBlocks.SIDEWALK.get());
        // The stone path comes off the stonecutter rather than a bench, and belongs to the
        // decorations part rather than to roadways.
        roads.recipesById("stone_path", recipeId("stone_path_stonecutting"))
                .whenPartEnabled(DecorConditions.Parts.DECORATIONS)
                .opens(DecorBlocks.STONE_PATH.get());
        roads.recipesById("rollers", recipeId(DecorItems.PAINT_ROLLER.get()), recipeId("white_wool_paint_roll"), recipeId("white_concrete_paint_roll")).every(60)
                .opens(rollers.toArray(Item[]::new))
                .whenPartEnabled(DecorConditions.Parts.PAINTING);
    }

    private void addDecorations() {
        Block[] siding = {DecorBlocks.SIDING_HORIZONTAL.get(), DecorBlocks.SIDING_VERTICAL.get()};

        ChapterBuilder decorations = this.chapter("decorations");
        decorations.recipes("clay", DecorItems.UNFIRED_CLAY_DECORATION.get(), DecorBlocks.CLAY_DECORATION.get()).every(50)
                .whenPartEnabled(DecorConditions.Parts.DECORATIONS)
                .opens(DecorBlocks.CLAY_DECORATION.get())
                .opens(DecorItems.UNFIRED_CLAY_DECORATION.get());
        // The pot is its own part, so it is its own page: a page may not draw a recipe that the
        // config has turned off, or it renders as a missing recipe.
        decorations.recipes("planter_pot", DecorItems.UNFIRED_PLANTER_POT.get(), DecorBlocks.PLANTER_POT.get()).every(50)
                .whenPartEnabled(DecorConditions.Parts.PLANTER_POT)
                .opens(DecorBlocks.PLANTER_POT.get())
                .opens(DecorItems.UNFIRED_PLANTER_POT.get());
        decorations.recipes("bone", DecorBlocks.BONE_DECORATION.get()).opens(DecorBlocks.BONE_DECORATION.get())
                .whenPartEnabled(DecorConditions.Parts.DECORATIONS);
        decorations.recipesById("stone", recipeId("decorative_path_stonecutting"), recipeId("stone_path_stonecutting")).every(60)
                .opens(DecorBlocks.DECORATIVE_STONE.get())
                .whenPartEnabled(DecorConditions.Parts.DECORATIONS);
        decorations.recipesById("siding", recipeId("siding_horizontal_white"), recipeId("siding_vertical_white")).every(60).opens(siding)
                .whenPartEnabled(DecorConditions.Parts.PAINTING);
        decorations.recipes("fountain", DecorBlocks.FOUNTAIN.get()).opens(DecorBlocks.FOUNTAIN.get())
                .whenPartEnabled(DecorConditions.Parts.DECORATIONS);
        decorations.recipes("cage", DecorBlocks.CAGE.get()).opens(DecorBlocks.CAGE.get())
                .whenPartEnabled(DecorConditions.Parts.CAGE);
    }

    private void addGates() {
        ChapterBuilder chapter = this.chapter("gates").whenPartEnabled(DecorConditions.Parts.GATES);
        chapter.recipes("castle_gate", DecorItems.GATE_GRATING.get(), DecorBlocks.CASTLE_GATE.get(), DecorItems.GATE_TRUMPET.get()).every(60)
                .opens(DecorBlocks.CASTLE_GATE.get(), DecorItems.GATE_GRATING.get(), DecorItems.GATE_TRUMPET.get());
        chapter.recipes("garage_door", DecorItems.GARAGE_PANEL.get(), DecorBlocks.GARAGE_DOOR.get(), DecorItems.GARAGE_REMOTE.get()).every(60)
                .opens(DecorBlocks.GARAGE_DOOR.get(), DecorItems.GARAGE_PANEL.get(), DecorItems.GARAGE_REMOTE.get());
    }

    private void addDoors() {
        Block[] doors = {DecorBlocks.GLASS_DOOR.get(), DecorBlocks.QUARTZ_DOOR.get(), DecorBlocks.STEEL_DOOR.get()};

        ChapterBuilder chapter = this.chapter("doors").whenPartEnabled(DecorConditions.Parts.EXTRAS);
        chapter.recipes("chain_link", DecorItems.CHAIN_LINK.get(), DecorBlocks.CHAIN_LINK_FENCE.get(), DecorBlocks.CHAIN_LINK_DOOR.get()).every(60)
                .opens(DecorBlocks.CHAIN_LINK_DOOR.get(), DecorBlocks.CHAIN_LINK_FENCE.get())
                .opens(DecorItems.CHAIN_LINK.get());
        chapter.recipes("doors", DecorBlocks.GLASS_DOOR.get(), DecorBlocks.QUARTZ_DOOR.get(), DecorBlocks.STEEL_DOOR.get()).every(50).opens(doors);
    }

    private static <T> List<T> values(Map<DyeColor, ? extends IRegistryObject<? extends T>> registered) {
        return registered.values().stream().map(IRegistryObject::get).map(t -> (T) t).toList();
    }

    /** The screenshots under {@code textures/gui/manual}, sized to leave room for the text below. */
    private static Identifier picture(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/manual/" + name + ".png");
    }
}

package com.grim3212.assorted.decor.client.data;

import net.minecraft.world.item.DyeColor;
import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.decor.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A block, item or entity whose name is its id in title case needs
 * no line here (see {@link LibLanguageProvider}); these are the names that read differently, and
 * every key that is not a name.
 */
public class DecorLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public DecorLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assorteddecor", "Assorted Decor");

        this.add("assorteddecor.container.cage", "Cage");

        this.add("tooltip.colorizer_brush.empty", "Empty");
        this.add("tooltip.colorizer_brush.stored", "Stored: %s");

        this.add("block.assorteddecor.neon_sign_wall", "Neon Sign");
        this.add("block.assorteddecor.colorizer_trap_door", "Colorizer Trapdoor");
        this.add("block.assorteddecor.colorizer_firepit_covered", "Colorizer Covered Firepit");

        this.add("screen.assorteddecor.neon_sign.bold", "Bold");
        this.add("screen.assorteddecor.neon_sign.italic", "Italic");
        this.add("screen.assorteddecor.neon_sign.underline", "Underline");
        this.add("screen.assorteddecor.neon_sign.strikethrough", "Strikethrough");
        this.add("screen.assorteddecor.neon_sign.random", "Obfuscated");
        this.add("screen.assorteddecor.neon_sign.reset", "Reset");

        this.add("tag.item.assorteddecor.fluro", "Fluro Blocks");
        this.add("tag.item.assorteddecor.lantern_source", "Lantern Light Sources");
        this.add("tag.item.assorteddecor.paint_rollers", "Paint Rollers");
        this.add("tag.item.assorteddecor.roadways", "Roadways");
        this.add("tag.item.assorteddecor.roadways.all", "All Roadways");
        this.add("tag.item.assorteddecor.roadways.color", "Colored Roadways");
        this.add("tag.item.c.tar", "Tar");

        // A siding is one block per direction, coloured by its state, and each colour is named by a
        // key of its own rather than a block.
        this.add("block.assorteddecor.siding_vertical", "Vertical Siding");
        this.add("block.assorteddecor.siding_horizontal", "Horizontal Siding");
        for (DyeColor color : DyeColor.values()) {
            this.add("block.assorteddecor.siding_vertical_" + color.getName(), titleCase(color.getName()) + " Vertical Siding");
            this.add("block.assorteddecor.siding_horizontal_" + color.getName(), titleCase(color.getName()) + " Horizontal Siding");
        }

        // Families whose names read differently from their ids.
        this.nameItems("paint_roller_" + dyeColors(), m -> titleCase(m.group(1)) + " Paint Roller");
        this.nameBlocks("roadway_" + dyeColors(), m -> titleCase(m.group(1)) + " Roadway");
        this.nameBlocks("fluro_" + dyeColors(), m -> titleCase(m.group(1)) + " Fluro Tube");

        this.addManual();
    }

    /** The chapters in {@code assets/assorteddecor/manual} name these keys. */
    private void addManual() {
        this.add("manual.assorteddecor.title", "Assorted Decor");
        this.add("manual.assorteddecor.description",
                "Blocks that take on the look of other blocks, plus furniture, lights, decorations, wall art, roads and more.");

        this.addColorizerChapter();
        this.addFurnitureChapter();
        this.addFiresChapter();
        this.addLightsChapter();
        this.addHangingChapter();
        this.addRoadsChapter();
        this.addDecorationsChapter();
        this.addDoorsChapter();
    }

    private void addColorizerChapter() {
        this.add("manual.assorteddecor.chapter.colorizer", "Colorizers");

        this.add("manual.assorteddecor.chapter.colorizer.colorizer.title", "Colorizers");
        this.add("manual.assorteddecor.chapter.colorizer.colorizer",
                "A colorizer is a blank block that borrows another block's texture." +BREAK
                        + "Everything in this chapter and the two after it is built from colorizers, which is "
                        + "why a chair, a slope and a fireplace can all match the wall behind them.");

        this.add("manual.assorteddecor.chapter.colorizer.brush.title", "Colorizer Brush");
        this.add("manual.assorteddecor.chapter.colorizer.brush",
                "A colorized block can only be set by being brushed with the Colorizer Brush." + BREAK
                        + "Shift right click a supported full block and the brush will take the blocks texture. Then right click with the brush on a Colorizer and see it get applied.");

        this.add("manual.assorteddecor.chapter.colorizer.shapes.title", "Basic Shapes");
        this.add("manual.assorteddecor.chapter.colorizer.shapes",
                "Slabs, vertical slabs, stairs, walls, fences and fence gates, all colorizable.");

        this.add("manual.assorteddecor.chapter.colorizer.slopes.title", "Slopes");
        this.add("manual.assorteddecor.chapter.colorizer.slopes",
                "Slopes are the angled half of the set. A slope is half a block cut corner to corner and walks "
                        + "like straight stairs; a sloped angle can only be walked up on the point it faces." + BREAK
                        + "Sloped intersections act as corner stairs, oblique slopes fill the corner a "
                        + "staircase leaves, slanted corners are a steeper climb, and sloped posts cannot be "
                        + "climbed at all. Corners are a sideways cut of a full block." + BREAK
                        + "All of them can be flipped upside down as you place them, the way a slab is.");

        this.add("manual.assorteddecor.chapter.colorizer.pyramids.title", "Pyramids");
        this.add("manual.assorteddecor.chapter.colorizer.pyramids",
                "A pyramid takes up half a block, and a large pyramid takes a full one and can be climbed, "
                        + "with its high point in the middle.");

        this.add("manual.assorteddecor.chapter.colorizer.doors.title", "Doors and Trap Doors");
        this.add("manual.assorteddecor.chapter.colorizer.doors",
                "A colorized door wearing the same block as the wall around it is a hidden entrance, and a "
                        + "colorized trap door does the same for a floor.");
    }

    private void addFurnitureChapter() {
        this.add("manual.assorteddecor.chapter.furniture", "Furniture");

        this.add("manual.assorteddecor.chapter.furniture.tables.title", "Tables and Counters");
        this.add("manual.assorteddecor.chapter.furniture.tables",
                "Tables placed next to each other work out where their legs belong, so a long table has room "
                        + "underneath it." + BREAK
                        + "A counter is a table without the legs, for running along a wall.");

        this.add("manual.assorteddecor.chapter.furniture.seats.title", "Chairs and Stools");
        this.add("manual.assorteddecor.chapter.furniture.seats",
                "Chairs turn to face the way you place them. Stools are the shorter version, and a planter pot "
                        + "sits on stool nicely.");

        this.add("manual.assorteddecor.chapter.furniture.lamp_post.title", "Lamp Posts");
        this.add("manual.assorteddecor.chapter.furniture.lamp_post",
                "A lamp post builds itself three blocks high from the one you place. And the top gives light.");
    }

    private void addFiresChapter() {
        this.add("manual.assorteddecor.chapter.fires", "Fires");

        this.add("manual.assorteddecor.chapter.fires.fireplace.title", "Fireplace");
        this.add("manual.assorteddecor.chapter.fires.fireplace",
                "Light a fireplace with flint and steel and it burns and gives light. Punch it to put it out."
                        + BREAK
                        + "Fireplaces placed side by side move their corner posts out of each other's way, so a "
                        + "row reads as one wide hearth.");

        this.add("manual.assorteddecor.chapter.fires.stove.title", "Stove");
        this.add("manual.assorteddecor.chapter.fires.stove",
                "A stove lights and goes out the same way a fireplace does, and takes a chimney on top.");

        this.add("manual.assorteddecor.chapter.fires.firepit.title", "Firepits");
        this.add("manual.assorteddecor.chapter.fires.firepit",
                "A firepit is the outdoor version, lit with flint and steel and put out with a punch. The covered one has a net over it.");

        this.add("manual.assorteddecor.chapter.fires.firering.title", "Fire Ring");
        this.add("manual.assorteddecor.chapter.fires.firering",
                "A fire ring is a ring of stones around a fire, lit and put out like the rest. Good for ghost stories.");

        this.add("manual.assorteddecor.chapter.fires.chimney.title", "Chimney");
        this.add("manual.assorteddecor.chapter.fires.chimney",
                "A chimney on a lit fireplace smokes. They stack, so the smoke can be carried up through a roof.");
    }

    private void addLightsChapter() {
        this.add("manual.assorteddecor.chapter.lights", "Lights");

        this.add("manual.assorteddecor.chapter.lights.fluro.title", "Fluro Blocks");
        this.add("manual.assorteddecor.chapter.lights.fluro",
                "Fluro blocks are bright, flat and come in all sixteen colors.");

        this.add("manual.assorteddecor.chapter.lights.illumination.title", "Illumination Tubes");
        this.add("manual.assorteddecor.chapter.lights.illumination",
                "Illumination tubes and plates light a room the way a torch does, but go on any face of a block and sit flush against it.");

        this.add("manual.assorteddecor.chapter.lights.lanterns.title", "Lanterns");
        this.add("manual.assorteddecor.chapter.lights.lanterns",
                "Bone, iron and paper lanterns, for when a torch is the wrong look for the room.");
    }

    private void addHangingChapter() {
        this.add("manual.assorteddecor.chapter.hanging", "On the Wall");

        this.add("manual.assorteddecor.chapter.hanging.wallpaper.title", "Wallpaper");
        this.add("manual.assorteddecor.chapter.hanging.wallpaper",
                "Wallpaper covers a wall without taking up the block. Once it is up, right click to cycle "
                        + "through the designs, and right click with a dye to color it." + BREAK
                        + "Wallpaper next to wallpaper tries to match its neighbours, so a whole wall lines up "
                        + "on its own.");

        this.add("manual.assorteddecor.chapter.hanging.frames.title", "Frames");
        this.add("manual.assorteddecor.chapter.hanging.frames",
                "Frames add depth where wallpaper adds pattern, in wood or iron, and right click to cycle through their "
                        + "patterns the same way." + BREAK
                        + "A frame is sturdier than wallpaper: take the block out from behind it and it stays "
                        + "where it is.");

        this.add("manual.assorteddecor.chapter.hanging.frame_info.title", "Dyeing Frames");
        this.add("manual.assorteddecor.chapter.hanging.frame_info",
                "Right click a hung frame with a dye and it takes that color, so a wall can be framed in one shade or in several.");

        this.add("manual.assorteddecor.chapter.hanging.calendar.title", "Calendar");
        this.add("manual.assorteddecor.chapter.hanging.calendar",
                "A calendar on the wall keeps count of the days you have been in the world.");

        this.add("manual.assorteddecor.chapter.hanging.clock.title", "Wall Clock");
        this.add("manual.assorteddecor.chapter.hanging.clock",
                "A wall clock shows the time of day.");

        this.add("manual.assorteddecor.chapter.hanging.neon_sign.title", "Neon Sign");
        this.add("manual.assorteddecor.chapter.hanging.neon_sign",
                "A neon sign works like an ordinary sign, except colored text glows and there is a proper "
                        + "editor for getting the layout right. Three backgrounds to choose from.");
    }

    private void addRoadsChapter() {
        this.add("manual.assorteddecor.chapter.roads", "Roads");

        this.add("manual.assorteddecor.chapter.roads.roadway.title", "Roadway");
        this.add("manual.assorteddecor.chapter.roads.roadway",
                "Roadway is asphalt laid flat." + BREAK
                        + "It comes in all sixteen colors for markings, plus a lit version and a manhole, so a "
                        + "road can be striped like a real one.");

        this.add("manual.assorteddecor.chapter.roads.asphalt.title", "Asphalt and Tar");
        this.add("manual.assorteddecor.chapter.roads.asphalt",
                "Tarballs make asphalt, and asphalt is what every road block here is built from.");

        this.add("manual.assorteddecor.chapter.roads.sidewalk.title", "Sidewalks and Paths");
        this.add("manual.assorteddecor.chapter.roads.sidewalk",
                "Sidewalk allow you to walk faster than you otherwise would and paths just look nice. 🙂");

        this.add("manual.assorteddecor.chapter.roads.stone_path.title", "Stone Path");
        this.add("manual.assorteddecor.chapter.roads.stone_path",
                "A stone path is the quieter surface for a garden, cut from plain stone on the "
                        + "stonecutter.");

        this.add("manual.assorteddecor.chapter.roads.rollers.title", "Paint Rollers");
        this.add("manual.assorteddecor.chapter.roads.rollers",
                "A paint roller loaded with a dye is how road markings and siding get their color. Use it on wool, "
                        + "carpet, concrete or concrete powder to recolor it as well." + BREAK
                        + "There is a roller for every color.");
    }

    private void addDecorationsChapter() {
        this.add("manual.assorteddecor.chapter.decorations", "Decorations");

        this.add("manual.assorteddecor.chapter.decorations.clay.title", "Clay Decorations");
        this.add("manual.assorteddecor.chapter.decorations.clay",
                "Clay decorations are crafted unfired and have to go through a furnace before "
                        + "they are any use." + BREAK
                        + "Once placed, right click to cycle through the options.");

        this.add("manual.assorteddecor.chapter.decorations.planter_pot.title", "Planter Pots");
        this.add("manual.assorteddecor.chapter.decorations.planter_pot",
                "A planter pot is crafted unfired and has to go through a furnace before it will hold "
                        + "anything." + BREAK
                        + "Once placed, right click to cycle through the filling material. The pot supports whichever "
                        + "plants suit the filling material it is set to.");

        this.add("manual.assorteddecor.chapter.decorations.bone.title", "Bone Decorations");
        this.add("manual.assorteddecor.chapter.decorations.bone",
                "A bone decoration needs no firing. Place it and right click to cycle through the shapes.");

        this.add("manual.assorteddecor.chapter.decorations.stone.title", "Decorative Stone");
        this.add("manual.assorteddecor.chapter.decorations.stone",
                "Decorative stone and stone paths both come off the stonecutter from plain stone, which makes "
                        + "them cheap enough to use by the wall.");

        this.add("manual.assorteddecor.chapter.decorations.siding.title", "Siding");
        this.add("manual.assorteddecor.chapter.decorations.siding",
                "Siding boards a wall in horizontal or vertical planking, in any of the sixteen colors. The "
                        + "color comes from the paint roller in the recipe.");

        this.add("manual.assorteddecor.chapter.decorations.fountain.title", "Fountain");
        this.add("manual.assorteddecor.chapter.decorations.fountain",
                "A fountain block pushes water up out of itself, for a courtyard that needed a middle.");

        this.add("manual.assorteddecor.chapter.decorations.cage.title", "Cage");
        this.add("manual.assorteddecor.chapter.decorations.cage",
                "A cage displays whatever you put in it, turning slowly. Use it with a Pokeball from Assorted Tools to show the caught monster.");
    }

    private void addDoorsChapter() {
        this.add("manual.assorteddecor.chapter.doors", "Doors and Fences");

        this.add("manual.assorteddecor.chapter.doors.chain_link.title", "Chain Link");
        this.add("manual.assorteddecor.chapter.doors.chain_link",
                "Chain link makes a fence you can see through");

        this.add("manual.assorteddecor.chapter.doors.doors.title", "Doors");
        this.add("manual.assorteddecor.chapter.doors.doors",
                "Glass, quartz and steel doors, for the places a plank door looks wrong. All three can take a "
                        + "padlock from Assorted Storage like any other door.");
    }
}

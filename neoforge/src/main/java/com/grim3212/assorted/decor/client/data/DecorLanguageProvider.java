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
    }
}

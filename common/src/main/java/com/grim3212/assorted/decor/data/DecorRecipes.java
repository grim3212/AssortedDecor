package com.grim3212.assorted.decor.data;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.api.DecorTags;
import com.grim3212.assorted.decor.common.blocks.ColorChangingBlock;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.FluroBlock;
import com.grim3212.assorted.decor.common.crafting.DecorConditions;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.decor.common.items.PaintRollerItem;
import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.DyeHelper;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class DecorRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public DecorRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, Constants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void registerConditions() {
        // Extra conditions for tag availability
        this.addConditions(itemTagExists(DecorTags.Items.INGOTS_STEEL), DecorBlocks.STEEL_DOOR.getId(), prefix("chain_link_steel"), prefix("steel_roadway_manhole"), prefix("fountain_steel"));
        this.addConditions(itemTagExists(DecorTags.Items.INGOTS_ALUMINUM), DecorBlocks.ILLUMINATION_PLATE.getId(), DecorBlocks.ILLUMINATION_TUBE.getId(), prefix("chain_link_aluminum"), prefix("fountain_aluminum"));

        // Roadways
        this.addConditions(partEnabled(DecorConditions.Parts.ROADWAYS), DecorBlocks.SIDEWALK.getId(), DecorItems.ASPHALT.getId(), DecorBlocks.ROADWAY.getId(), DecorBlocks.ROADWAY_MANHOLE.getId(), DecorBlocks.ROADWAY_LIGHT.getId(), DecorItems.TARBALL.getId(), prefix("steel_roadway_manhole"), prefix(DecorBlocks.ROADWAY.getId().getPath() + "_wash"));
        DecorBlocks.ROADWAY_COLORS.forEach((c, r) -> {
            this.addConditions(and(partEnabled(DecorConditions.Parts.ROADWAYS), partEnabled(DecorConditions.Parts.PAINTING)), r.getId());
        });

        // Painting
        this.addConditions(partEnabled(DecorConditions.Parts.PAINTING), DecorItems.PAINT_ROLLER.getId());
        DecorItems.PAINT_ROLLER_COLORS.forEach((c, r) -> {
            this.addConditions(partEnabled(DecorConditions.Parts.PAINTING), r.getId(), prefix(name(DyeHelper.WOOL_BY_DYE.get(c).asItem()) + "_paint_roll"), prefix(name(DyeHelper.CONCRETE_BY_DYE.get(c).asItem()) + "_paint_roll"), prefix(name(DyeHelper.CONCRETE_POWDER_BY_DYE.get(c).asItem()) + "_paint_roll"), prefix(name(DyeHelper.CARPET_BY_DYE.get(c).asItem()) + "_paint_roll"), prefix(name(FluroBlock.FLURO_BY_DYE.get(c).get()) + "_paint_roll"), prefix("siding_vertical_" + c.getName()), prefix("siding_horizontal_" + c.getName()));
        });

        // Self-explanatory
        this.addConditions(partEnabled(DecorConditions.Parts.HANGEABLES), DecorItems.WALLPAPER.getId(), DecorItems.WOOD_FRAME.getId(), DecorItems.IRON_FRAME.getId(), DecorBlocks.CALENDAR.getId(), DecorBlocks.WALL_CLOCK.getId(), prefix("wall_clock_alt"));
        this.addConditions(partEnabled(DecorConditions.Parts.NEON_SIGN), DecorItems.NEON_SIGN.getId());
        this.addConditions(partEnabled(DecorConditions.Parts.CAGE), DecorBlocks.CAGE.getId());
        this.addConditions(partEnabled(DecorConditions.Parts.PLANTER_POT), DecorBlocks.PLANTER_POT.getId(), DecorItems.UNFIRED_PLANTER_POT.getId());
        this.addConditions(partEnabled(DecorConditions.Parts.DECORATIONS), DecorItems.UNFIRED_CLAY_DECORATION.getId(), DecorBlocks.BONE_DECORATION.getId(), DecorBlocks.PAPER_LANTERN.getId(), DecorBlocks.BONE_LANTERN.getId(), DecorBlocks.IRON_LANTERN.getId(), DecorBlocks.CLAY_DECORATION.getId(), prefix("decorative_path_stonecutting"), prefix("stone_path_stonecutting"), prefix("fountain_aluminum"), prefix("fountain_steel"), DecorBlocks.FOUNTAIN.getId());
        this.addConditions(partEnabled(DecorConditions.Parts.GATES), DecorItems.GATE_GRATING.getId(), DecorItems.GARAGE_PANEL.getId(), DecorItems.GATE_TRUMPET.getId(), DecorItems.GARAGE_REMOTE.getId(), DecorBlocks.CASTLE_GATE.getId(), DecorBlocks.GARAGE_DOOR.getId());
        this.addConditions(partEnabled(DecorConditions.Parts.EXTRAS), DecorItems.CHAIN_LINK.getId(), DecorBlocks.CHAIN_LINK_FENCE.getId(), DecorBlocks.QUARTZ_DOOR.getId(), DecorBlocks.GLASS_DOOR.getId(), DecorBlocks.CHAIN_LINK_DOOR.getId(), DecorBlocks.STEEL_DOOR.getId(), prefix("chain_link_steel"), prefix("chain_link_aluminum"));

        // Fluro
        this.addConditions(partEnabled(DecorConditions.Parts.FLURO), prefix("illumination_tube_iron"), prefix("illumination_plate_iron"), DecorBlocks.ILLUMINATION_PLATE.getId(), DecorBlocks.ILLUMINATION_TUBE.getId());
        FluroBlock.FLURO_BY_DYE.entrySet().stream().forEach((x) -> {
            this.addConditions(partEnabled(DecorConditions.Parts.FLURO), id(x.getValue().get()));
        });

        // Colorizer
        this.addConditions(partEnabled(DecorConditions.Parts.COLORIZER), prefix("clean_colorizer_brush"), DecorItems.COLORIZER_BRUSH.getId(), prefix("colorizer_slab_stonecutting"), prefix("colorizer_vertical_slab_stonecutting"), prefix("colorizer_stairs_stonecutting"),
                prefix("colorizer_walls_stonecutting"), prefix("colorizer_chair_stonecutting"), prefix("colorizer_table_stonecutting"), prefix("colorizer_slope_stonecutting"), prefix("colorizer_sloped_angle_stonecutting"), prefix("colorizer_sloped_intersection_stonecutting"),
                prefix("colorizer_sloped_post_stonecutting"), prefix("colorizer_oblique_slope_stonecutting"), prefix("colorizer_corner_stonecutting"), prefix("colorizer_slanted_corner_stonecutting"), prefix("colorizer_pyramid_stonecutting"), prefix("colorizer_full_pyramid_stonecutting"));
        DecorBlocks.colorizerBlocks().forEach(b -> {
            this.addConditions(partEnabled(DecorConditions.Parts.COLORIZER), id(b.get()));
        });

    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        // Roadways
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.SIDEWALK.get(), 6).define('X', LibCommonTags.Items.STONE).pattern("XXX").pattern("XXX").unlockedBy("has_stone", has(LibCommonTags.Items.STONE)).save(this.output);
        SimpleCookingRecipeBuilder.smelting(this.tag(DecorTags.Items.TAR), RecipeCategory.DECORATIONS, CookingBookCategory.MISC, DecorItems.ASPHALT.get(), 0.35f, 200).unlockedBy("has_tar", has(DecorTags.Items.TAR)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.ROADWAY.get()).define('A', DecorItems.ASPHALT.get()).define('X', LibCommonTags.Items.STONE).pattern("A").pattern("X").unlockedBy("has_asphalt", has(DecorItems.ASPHALT.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.ROADWAY_MANHOLE.get()).define('M', LibCommonTags.Items.INGOTS_IRON).define('X', DecorBlocks.ROADWAY.get()).pattern("M").pattern("X").unlockedBy("has_roadway", has(DecorBlocks.ROADWAY.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.ROADWAY_LIGHT.get()).define('M', DecorBlocks.ILLUMINATION_PLATE.get()).define('X', DecorBlocks.ROADWAY.get()).pattern("M").pattern("X").unlockedBy("has_roadway", has(DecorBlocks.ROADWAY.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.ROADWAY_MANHOLE.get()).define('M', DecorTags.Items.INGOTS_STEEL).define('X', DecorBlocks.ROADWAY.get()).pattern("M").pattern("X").unlockedBy("has_roadway", has(DecorBlocks.ROADWAY.get())).save(this.output, key("steel_roadway_manhole"));
        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, DecorBlocks.ROADWAY.get()).requires(DecorTags.Items.ROADWAYS_COLOR).requires(fluid(FluidTags.WATER)).unlockedBy("has_roadway_color", has(DecorTags.Items.ROADWAYS_COLOR)).save(this.output, key(DecorBlocks.ROADWAY.getId().getPath() + "_wash"));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorItems.TARBALL.get(), 16).define('X', ItemTags.COALS).define('G', LibCommonTags.Items.GRAVEL).define('W', fluid(FluidTags.WATER)).pattern("X").pattern("G").pattern("W").unlockedBy("has_coal", has(ItemTags.COALS)).save(this.output);
        DecorBlocks.ROADWAY_COLORS.forEach((c, r) -> {
            PaintRollerItem matchingColor = DecorItems.PAINT_ROLLER_COLORS.get(c).get();
            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, r.get()).requires(DecorBlocks.ROADWAY.get()).requires(matchingColor).unlockedBy("has_paint", has(matchingColor)).save(this.output);
        });

        // Painting
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorItems.PAINT_ROLLER.get()).define('S', LibCommonTags.Items.RODS_WOODEN).define('W', ItemTags.WOOL).pattern("WWW").pattern(" S ").pattern(" S ").unlockedBy("has_wool", has(ItemTags.WOOL)).save(this.output);
        DecorItems.PAINT_ROLLER_COLORS.forEach((c, r) -> {
            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, DyeHelper.WOOL_BY_DYE.get(c)).requires(r.get()).requires(ItemTags.WOOL).unlockedBy("has_wool", has(ItemTags.WOOL)).save(this.output, key(name(DyeHelper.WOOL_BY_DYE.get(c).asItem()) + "_paint_roll"));
            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, DyeHelper.CONCRETE_BY_DYE.get(c)).requires(r.get()).requires(LibCommonTags.Items.CONCRETE).unlockedBy("has_concrete", has(LibCommonTags.Items.CONCRETE)).save(this.output, key(name(DyeHelper.CONCRETE_BY_DYE.get(c).asItem()) + "_paint_roll"));
            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, DyeHelper.CONCRETE_POWDER_BY_DYE.get(c)).requires(r.get()).requires(LibCommonTags.Items.CONCRETE_POWDER).unlockedBy("has_concrete_powder", has(LibCommonTags.Items.CONCRETE_POWDER)).save(this.output, key(name(DyeHelper.CONCRETE_POWDER_BY_DYE.get(c).asItem()) + "_paint_roll"));
            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, DyeHelper.CARPET_BY_DYE.get(c)).requires(r.get()).requires(ItemTags.WOOL_CARPETS).unlockedBy("has_carpet", has(ItemTags.WOOL_CARPETS)).save(this.output, key(name(DyeHelper.CARPET_BY_DYE.get(c).asItem()) + "_paint_roll"));
            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, FluroBlock.FLURO_BY_DYE.get(c).get()).requires(r.get()).requires(DecorTags.Items.FLURO).unlockedBy("has_fluro", has(DecorTags.Items.FLURO)).save(this.output, key(name(FluroBlock.FLURO_BY_DYE.get(c).get()) + "_paint_roll"));
            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, sidingResult(DecorBlocks.SIDING_VERTICAL.get().asItem(), c)).requires(DecorTags.Items.TAR).requires(LibCommonTags.Items.COBBLESTONE).requires(r.get()).unlockedBy("has_tar", has(DecorTags.Items.TAR)).save(this.output, key("siding_vertical_" + c.getName()));
            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, sidingResult(DecorBlocks.SIDING_HORIZONTAL.get().asItem(), c)).requires(DecorTags.Items.TAR).requires(ItemTags.PLANKS).requires(r.get()).unlockedBy("has_tar", has(DecorTags.Items.TAR)).save(this.output, key("siding_horizontal_" + c.getName()));
            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, r.get()).requires(DecorItems.PAINT_ROLLER.get()).requires(difference(this.tag(DyeHelper.getDyeTag(c)), this.tag(DecorTags.Items.PAINT_ROLLERS))).unlockedBy("has_dye", has(DyeHelper.getDyeTag(c))).save(this.output);
        });

        // Hangeables
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorItems.WALLPAPER.get()).define('X', ItemTags.WOOL).define('#', Items.PAPER).pattern("#X").pattern("#X").pattern("#X").unlockedBy("has_paper", has(Items.PAPER)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorItems.WOOD_FRAME.get()).define('X', ItemTags.PLANKS).pattern("  X").pattern(" X ").pattern("X  ").unlockedBy("has_planks", has(ItemTags.PLANKS)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorItems.IRON_FRAME.get()).define('X', LibCommonTags.Items.INGOTS_IRON).pattern("  X").pattern(" X ").pattern("X  ").unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.CALENDAR.get()).define('#', Items.PAPER).pattern("##").pattern("##").pattern("##").unlockedBy("has_paper", has(Items.PAPER)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.WALL_CLOCK.get()).define('#', ItemTags.PLANKS).define('C', Items.CLOCK).pattern("###").pattern("#C#").pattern("###").unlockedBy("has_clock", has(Items.CLOCK)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.WALL_CLOCK.get()).define('#', ItemTags.PLANKS).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('G', LibCommonTags.Items.INGOTS_GOLD).pattern("#G#").pattern("GRG").pattern("#G#").unlockedBy("has_redstone", has(LibCommonTags.Items.DUSTS_REDSTONE)).unlockedBy("has_gold", has(LibCommonTags.Items.INGOTS_GOLD)).save(this.output, key("wall_clock_alt"));

        // Neon Sign
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorItems.NEON_SIGN.get(), 3).define('X', LibCommonTags.Items.OBSIDIAN).define('G', ItemTags.PLANKS).define('C', LibCommonTags.Items.DUSTS_REDSTONE).pattern("XXX").pattern("XCX").pattern(" G ").unlockedBy("has_obsidian", has(LibCommonTags.Items.OBSIDIAN)).save(this.output);

        // Cage
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.CAGE.get(), 1).define('X', Items.IRON_BARS).pattern("XXX").pattern("X X").pattern("XXX").unlockedBy("has_iron_bars", has(Items.IRON_BARS)).save(this.output);

        // Planter Pot
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorItems.UNFIRED_PLANTER_POT.get()).define('X', Items.CLAY_BALL).pattern("X X").pattern("XXX").unlockedBy("has_clay", has(Items.CLAY_BALL)).save(this.output);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(DecorItems.UNFIRED_PLANTER_POT.get()), RecipeCategory.DECORATIONS, CookingBookCategory.MISC, DecorBlocks.PLANTER_POT.get(), 0.35f, 200).unlockedBy("has_unfired_planter_pot", has(DecorItems.UNFIRED_PLANTER_POT.get())).save(this.output);

        // Fluro
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.ILLUMINATION_TUBE.get(), 4).define('G', LibCommonTags.Items.GLASS).define('L', LibCommonTags.Items.DUSTS_GLOWSTONE).define('A', LibCommonTags.Items.INGOTS_IRON).pattern(" A ").pattern("GLG").pattern(" A ").unlockedBy("has_aluminum", has(LibCommonTags.Items.INGOTS_IRON)).unlockedBy("has_glowstone", has(LibCommonTags.Items.DUSTS_GLOWSTONE)).save(this.output, key("illumination_tube_iron"));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.ILLUMINATION_PLATE.get(), 8).define('G', LibCommonTags.Items.GLASS_PANES).define('L', DecorBlocks.ILLUMINATION_TUBE.get()).define('A', LibCommonTags.Items.INGOTS_IRON).pattern("GGG").pattern("ALA").pattern("GGG").unlockedBy("has_aluminum", has(LibCommonTags.Items.INGOTS_IRON)).unlockedBy("has_glowstone", has(LibCommonTags.Items.DUSTS_GLOWSTONE)).save(this.output, key("illumination_plate_iron"));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.ILLUMINATION_PLATE.get(), 8).define('G', LibCommonTags.Items.GLASS_PANES).define('L', DecorBlocks.ILLUMINATION_TUBE.get()).define('A', DecorTags.Items.INGOTS_ALUMINUM).pattern("GGG").pattern("ALA").pattern("GGG").unlockedBy("has_aluminum", has(DecorTags.Items.INGOTS_ALUMINUM)).unlockedBy("has_glowstone", has(LibCommonTags.Items.DUSTS_GLOWSTONE)).save(this.output, key(DecorBlocks.ILLUMINATION_PLATE.getId().getPath()));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.ILLUMINATION_TUBE.get(), 4).define('G', LibCommonTags.Items.GLASS).define('L', LibCommonTags.Items.DUSTS_GLOWSTONE).define('A', DecorTags.Items.INGOTS_ALUMINUM).pattern(" A ").pattern("GLG").pattern(" A ").unlockedBy("has_aluminum", has(DecorTags.Items.INGOTS_ALUMINUM)).unlockedBy("has_glowstone", has(LibCommonTags.Items.DUSTS_GLOWSTONE)).save(this.output, key(DecorBlocks.ILLUMINATION_TUBE.getId().getPath()));
        FluroBlock.FLURO_BY_DYE.entrySet().stream().forEach((x) -> {
            FluroBlock b = x.getValue().get();
            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, b, 4).define('G', LibCommonTags.Items.GLASS).define('L', DecorBlocks.ILLUMINATION_TUBE.get()).define('A', DyeHelper.getDyeTag(b.getColor())).pattern("GAG").pattern("ALA").pattern("GAG").unlockedBy("has_dye", has(LibCommonTags.Items.DYES)).unlockedBy("has_tube", has(DecorBlocks.ILLUMINATION_TUBE.get())).save(this.output);
        });

        // Decorations
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorItems.UNFIRED_CLAY_DECORATION.get()).define('X', Items.CLAY_BALL).pattern(" X ").pattern("XXX").pattern("XXX").unlockedBy("has_clay", has(Items.CLAY_BALL)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.BONE_DECORATION.get()).define('X', LibCommonTags.Items.BONES).pattern(" X ").pattern("XXX").pattern("XXX").unlockedBy("has_bones", has(LibCommonTags.Items.BONES)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.PAPER_LANTERN.get()).define('P', Items.PAPER).define('C', DecorTags.Items.LANTERN_SOURCE).pattern(" P ").pattern("PCP").unlockedBy("has_lantern_input", has(DecorTags.Items.LANTERN_SOURCE)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.BONE_LANTERN.get()).define('P', LibCommonTags.Items.BONES).define('C', DecorTags.Items.LANTERN_SOURCE).pattern(" P ").pattern("PCP").unlockedBy("has_lantern_input", has(DecorTags.Items.LANTERN_SOURCE)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.IRON_LANTERN.get()).define('P', LibCommonTags.Items.INGOTS_IRON).define('C', DecorTags.Items.LANTERN_SOURCE).pattern(" P ").pattern("PCP").unlockedBy("has_lantern_input", has(DecorTags.Items.LANTERN_SOURCE)).save(this.output);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(DecorItems.UNFIRED_CLAY_DECORATION.get()), RecipeCategory.DECORATIONS, CookingBookCategory.MISC, DecorBlocks.CLAY_DECORATION.get(), 0.35f, 200).unlockedBy("has_unfired_clay_decoration", has(DecorItems.UNFIRED_CLAY_DECORATION.get())).save(this.output);
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), RecipeCategory.DECORATIONS, DecorBlocks.DECORATIVE_STONE.get(), 1).unlockedBy("has_stone", has(Blocks.STONE)).save(this.output, key("decorative_path_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), RecipeCategory.DECORATIONS, DecorBlocks.STONE_PATH.get(), 1).unlockedBy("has_stone", has(Blocks.STONE)).save(this.output, key("stone_path_stonecutting"));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.FOUNTAIN.get()).define('X', LibCommonTags.Items.COBBLESTONE).define('W', fluid(FluidTags.WATER)).define('I', DecorTags.Items.INGOTS_ALUMINUM).pattern("XIX").pattern("XWX").pattern("XIX").unlockedBy("has_ingot", has(DecorTags.Items.INGOTS_ALUMINUM)).save(this.output, key("fountain_aluminum"));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.FOUNTAIN.get()).define('X', LibCommonTags.Items.COBBLESTONE).define('W', fluid(FluidTags.WATER)).define('I', DecorTags.Items.INGOTS_STEEL).pattern("XIX").pattern("XWX").pattern("XIX").unlockedBy("has_ingot", has(DecorTags.Items.INGOTS_STEEL)).save(this.output, key("fountain_steel"));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.FOUNTAIN.get()).define('X', LibCommonTags.Items.COBBLESTONE).define('W', fluid(FluidTags.WATER)).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("XIX").pattern("XWX").pattern("XIX").unlockedBy("has_ingot", has(LibCommonTags.Items.INGOTS_IRON)).save(this.output);

        // Extras
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorItems.CHAIN_LINK.get(), 4).define('X', LibCommonTags.Items.INGOTS_IRON).define('N', LibCommonTags.Items.NUGGETS_IRON).pattern(" N ").pattern("NXN").pattern(" N ").unlockedBy("has_ingot", has(LibCommonTags.Items.INGOTS_IRON)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.CHAIN_LINK_FENCE.get(), 8).define('X', DecorItems.CHAIN_LINK.get()).pattern("XXX").pattern("XXX").unlockedBy("has_chain_link", has(DecorItems.CHAIN_LINK.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.QUARTZ_DOOR.get(), 3).define('X', Items.QUARTZ).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_quartz", has(Items.QUARTZ)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.GLASS_DOOR.get(), 3).define('X', LibCommonTags.Items.GLASS).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_glass", has(LibCommonTags.Items.GLASS)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, DecorItems.GATE_GRATING.get(), 4).define('I', LibCommonTags.Items.INGOTS_IRON).pattern(" I ").pattern("III").pattern(" I ").unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, DecorItems.GARAGE_PANEL.get(), 4).define('I', LibCommonTags.Items.INGOTS_IRON).define('G', LibCommonTags.Items.GLASS).pattern("III").pattern(" G ").pattern("III").unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, DecorBlocks.CASTLE_GATE.get()).define('G', DecorItems.GATE_GRATING.get()).pattern("G").pattern("G").pattern("G").unlockedBy("has_gate_grating", has(DecorItems.GATE_GRATING.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, DecorBlocks.GARAGE_DOOR.get()).define('P', DecorItems.GARAGE_PANEL.get()).pattern("P").pattern("P").pattern("P").unlockedBy("has_garage_panel", has(DecorItems.GARAGE_PANEL.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.TOOLS, DecorItems.GATE_TRUMPET.get()).define('W', ItemTags.WOOL).define('G', LibCommonTags.Items.INGOTS_GOLD).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("G  ").pattern("WG ").pattern(" WI").unlockedBy("has_gate_grating", has(DecorItems.GATE_GRATING.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.TOOLS, DecorItems.GARAGE_REMOTE.get()).define('B', BlockItemTags.STONE_BUTTONS.item()).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("B").pattern("R").pattern("I").unlockedBy("has_garage_panel", has(DecorItems.GARAGE_PANEL.get())).save(this.output);

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.CHAIN_LINK_DOOR.get(), 3).define('X', DecorItems.CHAIN_LINK.get()).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_chain_link", has(DecorItems.CHAIN_LINK.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.STEEL_DOOR.get(), 3).define('X', DecorTags.Items.INGOTS_STEEL).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_steel", has(DecorTags.Items.INGOTS_STEEL)).save(this.output, key(DecorBlocks.STEEL_DOOR.getId().getPath()));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorItems.CHAIN_LINK.get(), 4).define('X', DecorTags.Items.INGOTS_STEEL).define('N', DecorTags.Items.NUGGETS_STEEL).pattern(" N ").pattern("NXN").pattern(" N ").unlockedBy("has_ingot", has(DecorTags.Items.INGOTS_STEEL)).save(this.output, key("chain_link_steel"));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorItems.CHAIN_LINK.get(), 4).define('X', DecorTags.Items.INGOTS_ALUMINUM).define('N', DecorTags.Items.NUGGETS_ALUMINUM).pattern(" N ").pattern("NXN").pattern(" N ").unlockedBy("has_ingot", has(DecorTags.Items.INGOTS_ALUMINUM)).save(this.output, key("chain_link_aluminum"));

        // Colorizer
        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, DecorItems.COLORIZER_BRUSH.get()).requires(fluid(FluidTags.WATER)).requires(DecorItems.COLORIZER_BRUSH.get()).unlockedBy("has_brush", has(DecorItems.COLORIZER_BRUSH.get())).save(this.output, key("clean_colorizer_brush"));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorItems.COLORIZER_BRUSH.get()).define('X', DecorBlocks.COLORIZER.get()).define('R', LibCommonTags.Items.RODS_WOODEN).define('S', LibCommonTags.Items.STRING).pattern(" SX").pattern(" RS").pattern("R  ").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER.get(), 4).define('X', LibCommonTags.Items.STONE).define('R', LibCommonTags.Items.DYES_RED).define('G', LibCommonTags.Items.DYES_GREEN).define('B', LibCommonTags.Items.DYES_BLUE).define('D', LibCommonTags.Items.DYES).pattern("XRX").pattern("GDB").pattern("XDX").unlockedBy("has_stone", has(LibCommonTags.Items.STONE)).unlockedBy("has_dye", has(LibCommonTags.Items.DYES)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_CHAIR.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("X  ").pattern("XXX").pattern("X X").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_TABLE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_STOOL.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('S', LibCommonTags.Items.RODS_WOODEN).pattern("XXX").pattern("S S").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_COUNTER.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('S', LibCommonTags.Items.RODS_WOODEN).pattern("XXX").pattern(" S ").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_FENCE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('S', LibCommonTags.Items.RODS_WOODEN).pattern("XSX").pattern("XSX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_FENCE_GATE.get(), 2).define('X', DecorBlocks.COLORIZER.get()).define('S', LibCommonTags.Items.RODS_WOODEN).pattern("SXS").pattern("SXS").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_WALL.get(), 6).define('X', DecorBlocks.COLORIZER.get()).pattern(" X ").pattern("XXX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_STAIRS.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("X  ").pattern("XX ").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLAB.get(), 6).define('X', DecorBlocks.COLORIZER.get()).pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_VERTICAL_SLAB.get(), 6).define('X', DecorBlocks.COLORIZER.get()).pattern("X").pattern("X").pattern("X").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_DOOR.get(), 3).define('X', DecorBlocks.COLORIZER.get()).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_TRAP_DOOR.get(), 2).define('X', DecorBlocks.COLORIZER.get()).pattern("XXX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_LAMP_POST.get(), 2).define('X', DecorBlocks.COLORIZER.get()).define('G', Blocks.GLOWSTONE).pattern("XGX").pattern("XXX").pattern(" X ").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).unlockedBy("has_glowstone", has(Blocks.GLOWSTONE)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLOPE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("  X").pattern(" XX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLOPED_ANGLE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern(" XX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLOPED_INTERSECTION.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("XX ").pattern("X X").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLOPED_POST.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("X ").pattern("XX").pattern("XX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_OBLIQUE_SLOPE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("  X").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_CORNER.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("XXX").pattern("XX ").pattern("X  ").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLANTED_CORNER.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("  X").pattern("  X").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_PYRAMID.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern(" X ").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_FULL_PYRAMID.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern(" X ").pattern(" X ").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_CHIMNEY.get(), 6).define('X', DecorBlocks.COLORIZER.get()).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("X X").pattern("X X").pattern("XIX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_FIREPIT.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('P', ItemTags.PLANKS).pattern("XPX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_FIREPIT_COVERED.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('I', Items.IRON_BARS).define('P', ItemTags.PLANKS).pattern("III").pattern("XPX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).unlockedBy("has_iron_bars", has(Items.IRON_BARS)).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_FIREPLACE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('P', ItemTags.PLANKS).pattern("XXX").pattern("XPX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_FIRERING.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('P', ItemTags.PLANKS).pattern(" X ").pattern("XPX").pattern(" X ").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_STOVE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('I', Items.IRON_BARS).define('P', ItemTags.PLANKS).pattern("XXX").pattern("IPI").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).unlockedBy("has_iron_bars", has(Items.IRON_BARS)).save(this.output);
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLAB.get(), 2).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output, key("colorizer_slab_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_VERTICAL_SLAB.get(), 2).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output, key("colorizer_vertical_slab_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_STAIRS.get(), 1).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output, key("colorizer_stairs_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_WALL.get(), 1).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output, key("colorizer_walls_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_CHAIR.get(), 1).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output, key("colorizer_chair_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_TABLE.get(), 1).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output, key("colorizer_table_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLOPE.get(), 1).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output, key("colorizer_slope_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLOPED_ANGLE.get(), 1).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output, key("colorizer_sloped_angle_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLOPED_INTERSECTION.get(), 1).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output, key("colorizer_sloped_intersection_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLOPED_POST.get(), 1).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output, key("colorizer_sloped_post_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_OBLIQUE_SLOPE.get(), 1).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output, key("colorizer_oblique_slope_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_CORNER.get(), 1).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output, key("colorizer_corner_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLANTED_CORNER.get(), 1).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output, key("colorizer_slanted_corner_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_PYRAMID.get(), 1).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output, key("colorizer_pyramid_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_FULL_PYRAMID.get(), 1).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(this.output, key("colorizer_full_pyramid_stonecutting"));
    }


    /**
     * Four coloured siding, as a recipe result. Datagen cannot build ItemStacks - an item's default
     * components are bound during a resource reload - so the colour rides along as a component patch.
     */
    private static ItemStackTemplate sidingResult(Item siding, net.minecraft.world.item.DyeColor color) {
        return new ItemStackTemplate(siding, ColorChangingBlock.getColorPatch(color)).withCount(4);
    }

    /**
     * Recipes are addressed by {@code ResourceKey<Recipe<?>>} rather than a raw id now.
     */
    private static ResourceKey<Recipe<?>> key(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, path));
    }

    private static ResourceKey<Recipe<?>> key(Identifier id) {
        return ResourceKey.create(Registries.RECIPE, id);
    }

    /**
     * Recipe providers are not data providers any more - a {@link RecipeProvider.Runner} owns the
     * file writing and builds a fresh provider around the {@link RecipeOutput} it hands out.
     */
    public static class Runner extends ConditionalRecipeProvider.Runner {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries, Constants.MOD_ID);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new DecorRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}

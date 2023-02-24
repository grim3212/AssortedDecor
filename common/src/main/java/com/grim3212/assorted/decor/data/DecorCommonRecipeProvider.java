package com.grim3212.assorted.decor.data;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.api.DecorTags;
import com.grim3212.assorted.decor.common.blocks.ColorChangingBlock;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.FluroBlock;
import com.grim3212.assorted.decor.common.crafting.DecorConditions;
import com.grim3212.assorted.decor.common.crafting.ShapelessItemStackBuilder;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.decor.common.items.PaintRollerItem;
import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.DyeHelper;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.Arrays;
import java.util.function.Consumer;

public class DecorCommonRecipeProvider extends ConditionalRecipeProvider {

    public DecorCommonRecipeProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
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
        Arrays.stream(DecorBlocks.colorizerBlocks()).forEach(b -> {
            this.addConditions(partEnabled(DecorConditions.Parts.COLORIZER), id(b));
        });

    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> consumer) {
        super.buildRecipes(consumer);

        // Roadways
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.SIDEWALK.get(), 6).define('X', LibCommonTags.Items.STONE).pattern("XXX").pattern("XXX").unlockedBy("has_stone", has(LibCommonTags.Items.STONE)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(DecorTags.Items.TAR), RecipeCategory.DECORATIONS, DecorItems.ASPHALT.get(), 0.35f, 200).unlockedBy("has_tar", has(DecorTags.Items.TAR)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.ROADWAY.get()).define('A', DecorItems.ASPHALT.get()).define('X', LibCommonTags.Items.STONE).pattern("A").pattern("X").unlockedBy("has_asphalt", has(DecorItems.ASPHALT.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.ROADWAY_MANHOLE.get()).define('M', LibCommonTags.Items.INGOTS_IRON).define('X', DecorBlocks.ROADWAY.get()).pattern("M").pattern("X").unlockedBy("has_roadway", has(DecorBlocks.ROADWAY.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.ROADWAY_LIGHT.get()).define('M', DecorBlocks.ILLUMINATION_PLATE.get()).define('X', DecorBlocks.ROADWAY.get()).pattern("M").pattern("X").unlockedBy("has_roadway", has(DecorBlocks.ROADWAY.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.ROADWAY_MANHOLE.get()).define('M', DecorTags.Items.INGOTS_STEEL).define('X', DecorBlocks.ROADWAY.get()).pattern("M").pattern("X").unlockedBy("has_roadway", has(DecorBlocks.ROADWAY.get())).save(consumer, prefix("steel_roadway_manhole"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, DecorBlocks.ROADWAY.get()).requires(DecorTags.Items.ROADWAYS_COLOR).requires(fluid(FluidTags.WATER)).unlockedBy("has_roadway_color", has(DecorTags.Items.ROADWAYS_COLOR)).save(consumer, prefix(DecorBlocks.ROADWAY.getId().getPath() + "_wash"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorItems.TARBALL.get(), 16).define('X', ItemTags.COALS).define('G', LibCommonTags.Items.GRAVEL).define('W', fluid(FluidTags.WATER)).pattern("X").pattern("G").pattern("W").unlockedBy("has_coal", has(ItemTags.COALS)).save(consumer);
        DecorBlocks.ROADWAY_COLORS.forEach((c, r) -> {
            PaintRollerItem matchingColor = DecorItems.PAINT_ROLLER_COLORS.get(c).get();
            ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, r.get()).requires(DecorBlocks.ROADWAY.get()).requires(matchingColor).unlockedBy("has_paint", has(matchingColor)).save(consumer);
        });

        // Painting
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorItems.PAINT_ROLLER.get()).define('S', LibCommonTags.Items.RODS_WOODEN).define('W', ItemTags.WOOL).pattern("WWW").pattern(" S ").pattern(" S ").unlockedBy("has_wool", has(ItemTags.WOOL)).save(consumer);
        DecorItems.PAINT_ROLLER_COLORS.forEach((c, r) -> {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, DyeHelper.WOOL_BY_DYE.get(c)).requires(r.get()).requires(ItemTags.WOOL).unlockedBy("has_wool", has(ItemTags.WOOL)).save(consumer, prefix(name(DyeHelper.WOOL_BY_DYE.get(c).asItem()) + "_paint_roll"));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, DyeHelper.CONCRETE_BY_DYE.get(c)).requires(r.get()).requires(LibCommonTags.Items.CONCRETE).unlockedBy("has_concrete", has(LibCommonTags.Items.CONCRETE)).save(consumer, prefix(name(DyeHelper.CONCRETE_BY_DYE.get(c).asItem()) + "_paint_roll"));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, DyeHelper.CONCRETE_POWDER_BY_DYE.get(c)).requires(r.get()).requires(LibCommonTags.Items.CONCRETE_POWDER).unlockedBy("has_concrete_powder", has(LibCommonTags.Items.CONCRETE_POWDER)).save(consumer, prefix(name(DyeHelper.CONCRETE_POWDER_BY_DYE.get(c).asItem()) + "_paint_roll"));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, DyeHelper.CARPET_BY_DYE.get(c)).requires(r.get()).requires(LibCommonTags.Items.CARPET).unlockedBy("has_carpet", has(LibCommonTags.Items.CARPET)).save(consumer, prefix(name(DyeHelper.CARPET_BY_DYE.get(c).asItem()) + "_paint_roll"));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, FluroBlock.FLURO_BY_DYE.get(c).get()).requires(r.get()).requires(DecorTags.Items.FLURO).unlockedBy("has_fluro", has(DecorTags.Items.FLURO)).save(consumer, prefix(name(FluroBlock.FLURO_BY_DYE.get(c).get()) + "_paint_roll"));
            ShapelessItemStackBuilder.shapeless(ColorChangingBlock.getColorStack(new ItemStack(DecorBlocks.SIDING_VERTICAL.get(), 4), c), RecipeCategory.DECORATIONS).requires(DecorTags.Items.TAR).requires(LibCommonTags.Items.COBBLESTONE).requires(r.get()).unlockedBy("has_tar", has(DecorTags.Items.TAR)).save(consumer, prefix("siding_vertical_" + c.getName()));
            ShapelessItemStackBuilder.shapeless(ColorChangingBlock.getColorStack(new ItemStack(DecorBlocks.SIDING_HORIZONTAL.get(), 4), c), RecipeCategory.DECORATIONS).requires(DecorTags.Items.TAR).requires(ItemTags.PLANKS).requires(r.get()).unlockedBy("has_tar", has(DecorTags.Items.TAR)).save(consumer, prefix("siding_horizontal_" + c.getName()));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, r.get()).requires(DecorItems.PAINT_ROLLER.get()).requires(difference(Ingredient.of(DyeHelper.getDyeTag(c)), Ingredient.of(DecorTags.Items.PAINT_ROLLERS))).unlockedBy("has_dye", has(DyeHelper.getDyeTag(c))).save(consumer);
        });

        // Hangeables
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorItems.WALLPAPER.get()).define('X', ItemTags.WOOL).define('#', Items.PAPER).pattern("#X").pattern("#X").pattern("#X").unlockedBy("has_paper", has(Items.PAPER)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorItems.WOOD_FRAME.get()).define('X', ItemTags.PLANKS).pattern("  X").pattern(" X ").pattern("X  ").unlockedBy("has_planks", has(ItemTags.PLANKS)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorItems.IRON_FRAME.get()).define('X', LibCommonTags.Items.INGOTS_IRON).pattern("  X").pattern(" X ").pattern("X  ").unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.CALENDAR.get()).define('#', Items.PAPER).pattern("##").pattern("##").pattern("##").unlockedBy("has_paper", has(Items.PAPER)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.WALL_CLOCK.get()).define('#', ItemTags.PLANKS).define('C', Items.CLOCK).pattern("###").pattern("#C#").pattern("###").unlockedBy("has_clock", has(Items.CLOCK)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.WALL_CLOCK.get()).define('#', ItemTags.PLANKS).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('G', LibCommonTags.Items.INGOTS_GOLD).pattern("#G#").pattern("GRG").pattern("#G#").unlockedBy("has_redstone", has(LibCommonTags.Items.DUSTS_REDSTONE)).unlockedBy("has_gold", has(LibCommonTags.Items.INGOTS_GOLD)).save(consumer, prefix("wall_clock_alt"));

        // Neon Sign
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorItems.NEON_SIGN.get(), 3).define('X', LibCommonTags.Items.OBSIDIAN).define('G', ItemTags.PLANKS).define('C', LibCommonTags.Items.DUSTS_REDSTONE).pattern("XXX").pattern("XCX").pattern(" G ").unlockedBy("has_obsidian", has(LibCommonTags.Items.OBSIDIAN)).save(consumer);

        // Cage
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.CAGE.get(), 1).define('X', Items.IRON_BARS).pattern("XXX").pattern("X X").pattern("XXX").unlockedBy("has_iron_bars", has(Items.IRON_BARS)).save(consumer);

        // Planter Pot
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorItems.UNFIRED_PLANTER_POT.get()).define('X', Items.CLAY_BALL).pattern("X X").pattern("XXX").unlockedBy("has_clay", has(Items.CLAY_BALL)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(DecorItems.UNFIRED_PLANTER_POT.get()), RecipeCategory.DECORATIONS, DecorBlocks.PLANTER_POT.get(), 0.35f, 200).unlockedBy("has_unfired_planter_pot", has(DecorItems.UNFIRED_PLANTER_POT.get())).save(consumer);

        // Fluro
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.ILLUMINATION_TUBE.get(), 4).define('G', LibCommonTags.Items.GLASS).define('L', LibCommonTags.Items.DUSTS_GLOWSTONE).define('A', LibCommonTags.Items.INGOTS_IRON).pattern(" A ").pattern("GLG").pattern(" A ").unlockedBy("has_aluminum", has(LibCommonTags.Items.INGOTS_IRON)).unlockedBy("has_glowstone", has(LibCommonTags.Items.DUSTS_GLOWSTONE)).save(consumer, prefix("illumination_tube_iron"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.ILLUMINATION_PLATE.get(), 8).define('G', LibCommonTags.Items.GLASS_PANES).define('L', DecorBlocks.ILLUMINATION_TUBE.get()).define('A', LibCommonTags.Items.INGOTS_IRON).pattern("GGG").pattern("ALA").pattern("GGG").unlockedBy("has_aluminum", has(LibCommonTags.Items.INGOTS_IRON)).unlockedBy("has_glowstone", has(LibCommonTags.Items.DUSTS_GLOWSTONE)).save(consumer, prefix("illumination_plate_iron"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.ILLUMINATION_PLATE.get(), 8).define('G', LibCommonTags.Items.GLASS_PANES).define('L', DecorBlocks.ILLUMINATION_TUBE.get()).define('A', DecorTags.Items.INGOTS_ALUMINUM).pattern("GGG").pattern("ALA").pattern("GGG").unlockedBy("has_aluminum", has(DecorTags.Items.INGOTS_ALUMINUM)).unlockedBy("has_glowstone", has(LibCommonTags.Items.DUSTS_GLOWSTONE)).save(consumer, DecorBlocks.ILLUMINATION_PLATE.getId());
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.ILLUMINATION_TUBE.get(), 4).define('G', LibCommonTags.Items.GLASS).define('L', LibCommonTags.Items.DUSTS_GLOWSTONE).define('A', DecorTags.Items.INGOTS_ALUMINUM).pattern(" A ").pattern("GLG").pattern(" A ").unlockedBy("has_aluminum", has(DecorTags.Items.INGOTS_ALUMINUM)).unlockedBy("has_glowstone", has(LibCommonTags.Items.DUSTS_GLOWSTONE)).save(consumer, DecorBlocks.ILLUMINATION_TUBE.getId());
        FluroBlock.FLURO_BY_DYE.entrySet().stream().forEach((x) -> {
            FluroBlock b = x.getValue().get();
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, b, 4).define('G', LibCommonTags.Items.GLASS).define('L', DecorBlocks.ILLUMINATION_TUBE.get()).define('A', DyeHelper.getDyeTag(b.getColor())).pattern("GAG").pattern("ALA").pattern("GAG").unlockedBy("has_dye", has(LibCommonTags.Items.DYES)).unlockedBy("has_tube", has(DecorBlocks.ILLUMINATION_TUBE.get())).save(consumer);
        });

        // Decorations
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorItems.UNFIRED_CLAY_DECORATION.get()).define('X', Items.CLAY_BALL).pattern(" X ").pattern("XXX").pattern("XXX").unlockedBy("has_clay", has(Items.CLAY_BALL)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.BONE_DECORATION.get()).define('X', LibCommonTags.Items.BONES).pattern(" X ").pattern("XXX").pattern("XXX").unlockedBy("has_bones", has(LibCommonTags.Items.BONES)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.PAPER_LANTERN.get()).define('P', Items.PAPER).define('C', DecorTags.Items.LANTERN_SOURCE).pattern(" P ").pattern("PCP").unlockedBy("has_lantern_input", has(DecorTags.Items.LANTERN_SOURCE)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.BONE_LANTERN.get()).define('P', LibCommonTags.Items.BONES).define('C', DecorTags.Items.LANTERN_SOURCE).pattern(" P ").pattern("PCP").unlockedBy("has_lantern_input", has(DecorTags.Items.LANTERN_SOURCE)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.IRON_LANTERN.get()).define('P', LibCommonTags.Items.INGOTS_IRON).define('C', DecorTags.Items.LANTERN_SOURCE).pattern(" P ").pattern("PCP").unlockedBy("has_lantern_input", has(DecorTags.Items.LANTERN_SOURCE)).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(DecorItems.UNFIRED_CLAY_DECORATION.get()), RecipeCategory.DECORATIONS, DecorBlocks.CLAY_DECORATION.get(), 0.35f, 200).unlockedBy("has_unfired_clay_decoration", has(DecorItems.UNFIRED_CLAY_DECORATION.get())).save(consumer);
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), RecipeCategory.DECORATIONS, DecorBlocks.DECORATIVE_STONE.get()).unlockedBy("has_stone", has(Blocks.STONE)).save(consumer, prefix("decorative_path_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), RecipeCategory.DECORATIONS, DecorBlocks.STONE_PATH.get()).unlockedBy("has_stone", has(Blocks.STONE)).save(consumer, prefix("stone_path_stonecutting"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.FOUNTAIN.get()).define('X', LibCommonTags.Items.COBBLESTONE).define('W', fluid(FluidTags.WATER)).define('I', DecorTags.Items.INGOTS_ALUMINUM).pattern("XIX").pattern("XWX").pattern("XIX").unlockedBy("has_ingot", has(DecorTags.Items.INGOTS_ALUMINUM)).save(consumer, prefix("fountain_aluminum"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.FOUNTAIN.get()).define('X', LibCommonTags.Items.COBBLESTONE).define('W', fluid(FluidTags.WATER)).define('I', DecorTags.Items.INGOTS_STEEL).pattern("XIX").pattern("XWX").pattern("XIX").unlockedBy("has_ingot", has(DecorTags.Items.INGOTS_STEEL)).save(consumer, prefix("fountain_steel"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.FOUNTAIN.get()).define('X', LibCommonTags.Items.COBBLESTONE).define('W', fluid(FluidTags.WATER)).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("XIX").pattern("XWX").pattern("XIX").unlockedBy("has_ingot", has(LibCommonTags.Items.INGOTS_IRON)).save(consumer);

        // Extras
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorItems.CHAIN_LINK.get(), 4).define('X', LibCommonTags.Items.INGOTS_IRON).define('N', LibCommonTags.Items.NUGGETS_IRON).pattern(" N ").pattern("NXN").pattern(" N ").unlockedBy("has_ingot", has(LibCommonTags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.CHAIN_LINK_FENCE.get(), 8).define('X', DecorItems.CHAIN_LINK.get()).pattern("XXX").pattern("XXX").unlockedBy("has_chain_link", has(DecorItems.CHAIN_LINK.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.QUARTZ_DOOR.get(), 3).define('X', Items.QUARTZ).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_quartz", has(Items.QUARTZ)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.GLASS_DOOR.get(), 3).define('X', LibCommonTags.Items.GLASS).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_glass", has(LibCommonTags.Items.GLASS)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.CHAIN_LINK_DOOR.get(), 3).define('X', DecorItems.CHAIN_LINK.get()).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_chain_link", has(DecorItems.CHAIN_LINK.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.STEEL_DOOR.get(), 3).define('X', DecorTags.Items.INGOTS_STEEL).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_steel", has(DecorTags.Items.INGOTS_STEEL)).save(consumer, DecorBlocks.STEEL_DOOR.getId());
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorItems.CHAIN_LINK.get(), 4).define('X', DecorTags.Items.INGOTS_STEEL).define('N', DecorTags.Items.NUGGETS_STEEL).pattern(" N ").pattern("NXN").pattern(" N ").unlockedBy("has_ingot", has(DecorTags.Items.INGOTS_STEEL)).save(consumer, prefix("chain_link_steel"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorItems.CHAIN_LINK.get(), 4).define('X', DecorTags.Items.INGOTS_ALUMINUM).define('N', DecorTags.Items.NUGGETS_ALUMINUM).pattern(" N ").pattern("NXN").pattern(" N ").unlockedBy("has_ingot", has(DecorTags.Items.INGOTS_ALUMINUM)).save(consumer, prefix("chain_link_aluminum"));

        // Colorizer
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, DecorItems.COLORIZER_BRUSH.get()).requires(fluid(FluidTags.WATER)).requires(DecorItems.COLORIZER_BRUSH.get()).unlockedBy("has_brush", has(DecorItems.COLORIZER_BRUSH.get())).save(consumer, prefix("clean_colorizer_brush"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorItems.COLORIZER_BRUSH.get()).define('X', DecorBlocks.COLORIZER.get()).define('R', LibCommonTags.Items.RODS_WOODEN).define('S', LibCommonTags.Items.STRING).pattern(" SX").pattern(" RS").pattern("R  ").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER.get(), 4).define('X', LibCommonTags.Items.STONE).define('R', LibCommonTags.Items.DYES_RED).define('G', LibCommonTags.Items.DYES_GREEN).define('B', LibCommonTags.Items.DYES_BLUE).define('D', LibCommonTags.Items.DYES).pattern("XRX").pattern("GDB").pattern("XDX").unlockedBy("has_stone", has(LibCommonTags.Items.STONE)).unlockedBy("has_dye", has(LibCommonTags.Items.DYES)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_CHAIR.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("X  ").pattern("XXX").pattern("X X").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_TABLE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_STOOL.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('S', LibCommonTags.Items.RODS_WOODEN).pattern("XXX").pattern("S S").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_COUNTER.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('S', LibCommonTags.Items.RODS_WOODEN).pattern("XXX").pattern(" S ").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_FENCE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('S', LibCommonTags.Items.RODS_WOODEN).pattern("XSX").pattern("XSX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_FENCE_GATE.get(), 2).define('X', DecorBlocks.COLORIZER.get()).define('S', LibCommonTags.Items.RODS_WOODEN).pattern("SXS").pattern("SXS").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_WALL.get(), 6).define('X', DecorBlocks.COLORIZER.get()).pattern(" X ").pattern("XXX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_STAIRS.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("X  ").pattern("XX ").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLAB.get(), 6).define('X', DecorBlocks.COLORIZER.get()).pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_VERTICAL_SLAB.get(), 6).define('X', DecorBlocks.COLORIZER.get()).pattern("X").pattern("X").pattern("X").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_DOOR.get(), 3).define('X', DecorBlocks.COLORIZER.get()).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_TRAP_DOOR.get(), 2).define('X', DecorBlocks.COLORIZER.get()).pattern("XXX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_LAMP_POST.get(), 2).define('X', DecorBlocks.COLORIZER.get()).define('G', Blocks.GLOWSTONE).pattern("XGX").pattern("XXX").pattern(" X ").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).unlockedBy("has_glowstone", has(Blocks.GLOWSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLOPE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("  X").pattern(" XX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLOPED_ANGLE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern(" XX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLOPED_INTERSECTION.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("XX ").pattern("X X").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLOPED_POST.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("X ").pattern("XX").pattern("XX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_OBLIQUE_SLOPE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("  X").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_CORNER.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("XXX").pattern("XX ").pattern("X  ").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLANTED_CORNER.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("  X").pattern("  X").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_PYRAMID.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern(" X ").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_FULL_PYRAMID.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern(" X ").pattern(" X ").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_CHIMNEY.get(), 6).define('X', DecorBlocks.COLORIZER.get()).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("X X").pattern("X X").pattern("XIX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_FIREPIT.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('P', ItemTags.PLANKS).pattern("XPX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_FIREPIT_COVERED.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('I', Items.IRON_BARS).define('P', ItemTags.PLANKS).pattern("III").pattern("XPX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).unlockedBy("has_iron_bars", has(Items.IRON_BARS)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_FIREPLACE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('P', ItemTags.PLANKS).pattern("XXX").pattern("XPX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_FIRERING.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('P', ItemTags.PLANKS).pattern(" X ").pattern("XPX").pattern(" X ").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_STOVE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('I', Items.IRON_BARS).define('P', ItemTags.PLANKS).pattern("XXX").pattern("IPI").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).unlockedBy("has_iron_bars", has(Items.IRON_BARS)).save(consumer);
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLAB.get(), 2).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_slab_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_VERTICAL_SLAB.get(), 2).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_vertical_slab_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_STAIRS.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_stairs_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_WALL.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_walls_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_CHAIR.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_chair_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_TABLE.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_table_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLOPE.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_slope_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLOPED_ANGLE.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_sloped_angle_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLOPED_INTERSECTION.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_sloped_intersection_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLOPED_POST.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_sloped_post_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_OBLIQUE_SLOPE.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_oblique_slope_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_CORNER.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_corner_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_SLANTED_CORNER.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_slanted_corner_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_PYRAMID.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_pyramid_stonecutting"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), RecipeCategory.DECORATIONS, DecorBlocks.COLORIZER_FULL_PYRAMID.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_full_pyramid_stonecutting"));
    }

}

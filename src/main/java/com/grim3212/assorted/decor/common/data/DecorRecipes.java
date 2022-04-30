package com.grim3212.assorted.decor.common.data;

import java.util.function.Consumer;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.ColorChangingBlock;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.block.FluroBlock;
import com.grim3212.assorted.decor.common.crafting.ShapelessItemStackBuilder;
import com.grim3212.assorted.decor.common.item.DecorItems;
import com.grim3212.assorted.decor.common.item.PaintRollerItem;
import com.grim3212.assorted.decor.common.lib.DecorTags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import net.minecraftforge.registries.ForgeRegistries;

public class DecorRecipes extends RecipeProvider {

	public DecorRecipes(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(DecorBlocks.SIDEWALK.get(), 6).define('X', Tags.Items.STONE).pattern("XXX").pattern("XXX").unlockedBy("has_stone", has(Tags.Items.STONE)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorItems.TARBALL.get(), 16).define('X', ItemTags.COALS).define('G', Tags.Items.GRAVEL).define('W', DecorTags.Items.BUCKETS_WATER).pattern("X").pattern("G").pattern("W").unlockedBy("has_coal", has(ItemTags.COALS)).save(consumer);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(DecorTags.Items.TAR), DecorItems.ASPHALT.get(), 0.35f, 200).unlockedBy("has_tar", has(DecorTags.Items.TAR)).save(consumer);

		ShapedRecipeBuilder.shaped(DecorBlocks.ROADWAY.get()).define('A', DecorItems.ASPHALT.get()).define('X', Tags.Items.STONE).pattern("A").pattern("X").unlockedBy("has_asphalt", has(DecorItems.ASPHALT.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.ROADWAY_MANHOLE.get()).define('M', Tags.Items.INGOTS_IRON).define('X', DecorBlocks.ROADWAY.get()).pattern("M").pattern("X").unlockedBy("has_roadway", has(DecorBlocks.ROADWAY.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.ROADWAY_MANHOLE.get()).define('M', DecorTags.Items.INGOTS_STEEL).define('X', DecorBlocks.ROADWAY.get()).pattern("M").pattern("X").unlockedBy("has_roadway", has(DecorBlocks.ROADWAY.get())).save(consumer, prefix("steel_roadway_manhole"));
		ShapedRecipeBuilder.shaped(DecorBlocks.ROADWAY_LIGHT.get()).define('M', DecorBlocks.ILLUMINATION_PLATE.get()).define('X', DecorBlocks.ROADWAY.get()).pattern("M").pattern("X").unlockedBy("has_roadway", has(DecorBlocks.ROADWAY.get())).save(consumer);

		ShapedRecipeBuilder.shaped(DecorItems.PAINT_ROLLER.get()).define('S', Tags.Items.RODS_WOODEN).define('W', ItemTags.WOOL).pattern("WWW").pattern(" S ").pattern(" S ").unlockedBy("has_wool", has(ItemTags.WOOL)).save(consumer);
		
		DecorItems.PAINT_ROLLER_COLORS.forEach((c, r) -> {
			ShapelessRecipeBuilder.shapeless(r.get()).requires(DecorItems.PAINT_ROLLER.get()).requires(DifferenceIngredient.of(Ingredient.of(c.getTag()), Ingredient.of(DecorTags.Items.PAINT_ROLLERS))).unlockedBy("has_dye", has(c.getTag())).save(consumer);
			ShapelessRecipeBuilder.shapeless(PaintRollerItem.WOOL_BY_DYE.get(c)).requires(r.get()).requires(ItemTags.WOOL).unlockedBy("has_wool", has(ItemTags.WOOL)).save(consumer, prefix(name(PaintRollerItem.WOOL_BY_DYE.get(c).asItem()) + "_paint_roll"));
			ShapelessRecipeBuilder.shapeless(PaintRollerItem.CONCRETE_BY_DYE.get(c)).requires(r.get()).requires(DecorTags.Items.CONCRETE).unlockedBy("has_concrete", has(DecorTags.Items.CONCRETE)).save(consumer, prefix(name(PaintRollerItem.CONCRETE_BY_DYE.get(c).asItem()) + "_paint_roll"));
			ShapelessRecipeBuilder.shapeless(PaintRollerItem.CONCRETE_POWDER_BY_DYE.get(c)).requires(r.get()).requires(DecorTags.Items.CONCRETE_POWDER).unlockedBy("has_concrete_powder", has(DecorTags.Items.CONCRETE_POWDER)).save(consumer, prefix(name(PaintRollerItem.CONCRETE_POWDER_BY_DYE.get(c).asItem()) + "_paint_roll"));
			ShapelessRecipeBuilder.shapeless(PaintRollerItem.CARPET_BY_DYE.get(c)).requires(r.get()).requires(DecorTags.Items.CARPET).unlockedBy("has_carpet", has(DecorTags.Items.CARPET)).save(consumer, prefix(name(PaintRollerItem.CARPET_BY_DYE.get(c).asItem()) + "_paint_roll"));
			ShapelessRecipeBuilder.shapeless(FluroBlock.FLURO_BY_DYE.get(c).get()).requires(r.get()).requires(DecorTags.Items.FLURO).unlockedBy("has_fluro", has(DecorTags.Items.FLURO)).save(consumer, prefix(name(FluroBlock.FLURO_BY_DYE.get(c).get()) + "_paint_roll"));
			ShapelessItemStackBuilder.shapeless(ColorChangingBlock.getColorStack(new ItemStack(DecorBlocks.SIDING_VERTICAL.get(), 4), c)).requires(DecorTags.Items.TAR).requires(Tags.Items.COBBLESTONE).requires(r.get()).unlockedBy("has_tar", has(DecorTags.Items.TAR)).save(consumer, prefix("siding_vertical_" + c.getName()));
			ShapelessItemStackBuilder.shapeless(ColorChangingBlock.getColorStack(new ItemStack(DecorBlocks.SIDING_HORIZONTAL.get(), 4), c)).requires(DecorTags.Items.TAR).requires(ItemTags.PLANKS).requires(r.get()).unlockedBy("has_tar", has(DecorTags.Items.TAR)).save(consumer, prefix("siding_horizontal_" + c.getName()));
		});

		DecorBlocks.ROADWAY_COLORS.forEach((c, r) -> {
			PaintRollerItem matchingColor = DecorItems.PAINT_ROLLER_COLORS.get(c).get();
			ShapelessRecipeBuilder.shapeless(r.get()).requires(DecorBlocks.ROADWAY.get()).requires(matchingColor).unlockedBy("has_paint", has(matchingColor)).save(consumer);
		});
		
		ShapelessRecipeBuilder.shapeless(DecorBlocks.ROADWAY.get()).requires(DecorTags.Items.ROADWAYS_COLOR).requires(DecorTags.Items.BUCKETS_WATER).unlockedBy("has_roadway_color", has(DecorTags.Items.ROADWAYS_COLOR)).save(consumer, prefix(DecorBlocks.ROADWAY.getId().getPath() + "_wash"));

		ShapedRecipeBuilder.shaped(DecorItems.WALLPAPER.get()).define('X', ItemTags.WOOL).define('#', Items.PAPER).pattern("#X").pattern("#X").pattern("#X").unlockedBy("has_paper", has(Items.PAPER)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorItems.WOOD_FRAME.get()).define('X', ItemTags.PLANKS).pattern("  X").pattern(" X ").pattern("X  ").unlockedBy("has_planks", has(ItemTags.PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorItems.IRON_FRAME.get()).define('X', Tags.Items.INGOTS_IRON).pattern("  X").pattern(" X ").pattern("X  ").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorItems.COLORIZER_BRUSH.get()).define('X', DecorBlocks.COLORIZER.get()).define('R', Tags.Items.RODS_WOODEN).define('S', Tags.Items.STRING).pattern(" SX").pattern(" RS").pattern("R  ").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapelessRecipeBuilder.shapeless(DecorItems.COLORIZER_BRUSH.get()).requires(DecorTags.Items.BUCKETS_WATER).requires(DecorItems.COLORIZER_BRUSH.get()).unlockedBy("has_brush", has(DecorItems.COLORIZER_BRUSH.get())).save(consumer, prefix("clean_colorizer_brush"));
		ShapedRecipeBuilder.shaped(DecorItems.NEON_SIGN.get(), 3).define('X', Tags.Items.OBSIDIAN).define('G', ItemTags.PLANKS).define('C', Tags.Items.DUSTS_REDSTONE).pattern("XXX").pattern("XCX").pattern(" G ").unlockedBy("has_obsidian", has(Tags.Items.OBSIDIAN)).save(consumer);

		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER.get(), 4).define('X', Tags.Items.STONE).define('R', Tags.Items.DYES_RED).define('G', Tags.Items.DYES_GREEN).define('B', Tags.Items.DYES_BLUE).define('D', Tags.Items.DYES).pattern("XRX").pattern("GDB").pattern("XDX").unlockedBy("has_stone", has(Tags.Items.STONE)).unlockedBy("has_dye", has(Tags.Items.DYES)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_CHAIR.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("X  ").pattern("XXX").pattern("X X").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_TABLE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_STOOL.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('S', Tags.Items.RODS_WOODEN).pattern("XXX").pattern("S S").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_COUNTER.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('S', Tags.Items.RODS_WOODEN).pattern("XXX").pattern(" S ").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_FENCE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('S', Tags.Items.RODS_WOODEN).pattern("XSX").pattern("XSX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_FENCE_GATE.get(), 2).define('X', DecorBlocks.COLORIZER.get()).define('S', Tags.Items.RODS_WOODEN).pattern("SXS").pattern("SXS").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_WALL.get(), 6).define('X', DecorBlocks.COLORIZER.get()).pattern(" X ").pattern("XXX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_STAIRS.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("X  ").pattern("XX ").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_SLAB.get(), 6).define('X', DecorBlocks.COLORIZER.get()).pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_VERTICAL_SLAB.get(), 6).define('X', DecorBlocks.COLORIZER.get()).pattern("X").pattern("X").pattern("X").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_DOOR.get(), 3).define('X', DecorBlocks.COLORIZER.get()).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_TRAP_DOOR.get(), 2).define('X', DecorBlocks.COLORIZER.get()).pattern("XXX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_LAMP_POST.get(), 2).define('X', DecorBlocks.COLORIZER.get()).define('G', Blocks.GLOWSTONE).pattern("XGX").pattern("XXX").pattern(" X ").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).unlockedBy("has_glowstone", has(Blocks.GLOWSTONE)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_SLOPE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("  X").pattern(" XX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_SLOPED_ANGLE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern(" XX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_SLOPED_INTERSECTION.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("XX ").pattern("X X").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_SLOPED_POST.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("X ").pattern("XX").pattern("XX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_OBLIQUE_SLOPE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("  X").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_CORNER.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("XXX").pattern("XX ").pattern("X  ").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_SLANTED_CORNER.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern("  X").pattern("  X").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_PYRAMID.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern(" X ").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_FULL_PYRAMID.get(), 4).define('X', DecorBlocks.COLORIZER.get()).pattern(" X ").pattern(" X ").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_CHIMNEY.get(), 6).define('X', DecorBlocks.COLORIZER.get()).define('I', Tags.Items.INGOTS_IRON).pattern("X X").pattern("X X").pattern("XIX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_FIREPIT.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('P', ItemTags.PLANKS).pattern("XPX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_FIREPIT_COVERED.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('I', Items.IRON_BARS).define('P', ItemTags.PLANKS).pattern("III").pattern("XPX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).unlockedBy("has_iron_bars", has(Items.IRON_BARS)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_FIREPLACE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('P', ItemTags.PLANKS).pattern("XXX").pattern("XPX").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_FIRERING.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('P', ItemTags.PLANKS).pattern(" X ").pattern("XPX").pattern(" X ").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.COLORIZER_STOVE.get(), 4).define('X', DecorBlocks.COLORIZER.get()).define('I', Items.IRON_BARS).define('P', ItemTags.PLANKS).pattern("XXX").pattern("IPI").pattern("XXX").unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).unlockedBy("has_iron_bars", has(Items.IRON_BARS)).save(consumer);

		ShapedRecipeBuilder.shaped(DecorBlocks.ILLUMINATION_TUBE.get(), 4).define('G', Tags.Items.GLASS).define('L', Tags.Items.DUSTS_GLOWSTONE).define('A', DecorTags.Items.INGOTS_ALUMINUM).pattern(" A ").pattern("GLG").pattern(" A ").unlockedBy("has_aluminum", has(DecorTags.Items.INGOTS_ALUMINUM)).unlockedBy("has_glowstone", has(Tags.Items.DUSTS_GLOWSTONE)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.ILLUMINATION_TUBE.get(), 4).define('G', Tags.Items.GLASS).define('L', Tags.Items.DUSTS_GLOWSTONE).define('A', Tags.Items.INGOTS_IRON).pattern(" A ").pattern("GLG").pattern(" A ").unlockedBy("has_aluminum", has(Tags.Items.INGOTS_IRON)).unlockedBy("has_glowstone", has(Tags.Items.DUSTS_GLOWSTONE)).save(consumer, prefix("illumination_tube_iron"));

		ShapedRecipeBuilder.shaped(DecorBlocks.ILLUMINATION_PLATE.get(), 8).define('G', Tags.Items.GLASS_PANES).define('L', DecorBlocks.ILLUMINATION_TUBE.get()).define('A', DecorTags.Items.INGOTS_ALUMINUM).pattern("GGG").pattern("ALA").pattern("GGG").unlockedBy("has_aluminum", has(DecorTags.Items.INGOTS_ALUMINUM)).unlockedBy("has_glowstone", has(Tags.Items.DUSTS_GLOWSTONE)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.ILLUMINATION_PLATE.get(), 8).define('G', Tags.Items.GLASS_PANES).define('L', DecorBlocks.ILLUMINATION_TUBE.get()).define('A', Tags.Items.INGOTS_IRON).pattern("GGG").pattern("ALA").pattern("GGG").unlockedBy("has_aluminum", has(Tags.Items.INGOTS_IRON)).unlockedBy("has_glowstone", has(Tags.Items.DUSTS_GLOWSTONE)).save(consumer, prefix("illumination_plate_iron"));

		ShapedRecipeBuilder.shaped(DecorItems.UNFIRED_PLANTER_POT.get()).define('X', Items.CLAY_BALL).pattern("X X").pattern("XXX").unlockedBy("has_clay", has(Items.CLAY_BALL)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorItems.UNFIRED_CLAY_DECORATION.get()).define('X', Items.CLAY_BALL).pattern(" X ").pattern("XXX").pattern("XXX").unlockedBy("has_clay", has(Items.CLAY_BALL)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.BONE_DECORATION.get()).define('X', Tags.Items.BONES).pattern(" X ").pattern("XXX").pattern("XXX").unlockedBy("has_bones", has(Tags.Items.BONES)).save(consumer);

		ShapedRecipeBuilder.shaped(DecorBlocks.PAPER_LANTERN.get()).define('P', Items.PAPER).define('C', DecorTags.Items.LANTERN_SOURCE).pattern(" P ").pattern("PCP").unlockedBy("has_lantern_input", has(DecorTags.Items.LANTERN_SOURCE)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.BONE_LANTERN.get()).define('P', Tags.Items.BONES).define('C', DecorTags.Items.LANTERN_SOURCE).pattern(" P ").pattern("PCP").unlockedBy("has_lantern_input", has(DecorTags.Items.LANTERN_SOURCE)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.IRON_LANTERN.get()).define('P', Tags.Items.INGOTS_IRON).define('C', DecorTags.Items.LANTERN_SOURCE).pattern(" P ").pattern("PCP").unlockedBy("has_lantern_input", has(DecorTags.Items.LANTERN_SOURCE)).save(consumer);

		SimpleCookingRecipeBuilder.smelting(Ingredient.of(DecorItems.UNFIRED_PLANTER_POT.get()), DecorBlocks.PLANTER_POT.get(), 0.35f, 200).unlockedBy("has_unfired_planter_pot", has(DecorItems.UNFIRED_PLANTER_POT.get())).save(consumer);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(DecorItems.UNFIRED_CLAY_DECORATION.get()), DecorBlocks.CLAY_DECORATION.get(), 0.35f, 200).unlockedBy("has_unfired_clay_decoration", has(DecorItems.UNFIRED_CLAY_DECORATION.get())).save(consumer);

		SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_SLAB.get(), 2).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_slab_stonecutting"));
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_VERTICAL_SLAB.get(), 2).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_vertical_slab_stonecutting"));
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_STAIRS.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_stairs_stonecutting"));
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_WALL.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_walls_stonecutting"));
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_CHAIR.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_chair_stonecutting"));
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_TABLE.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_table_stonecutting"));
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_SLOPE.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_slope_stonecutting"));
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_SLOPED_ANGLE.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_sloped_angle_stonecutting"));
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_SLOPED_INTERSECTION.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_sloped_intersection_stonecutting"));
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_SLOPED_POST.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_sloped_post_stonecutting"));
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_OBLIQUE_SLOPE.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_oblique_slope_stonecutting"));
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_CORNER.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_corner_stonecutting"));
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_SLANTED_CORNER.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_slanted_corner_stonecutting"));
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_PYRAMID.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_pyramid_stonecutting"));
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_FULL_PYRAMID.get()).unlockedBy("has_colorizer", has(DecorBlocks.COLORIZER.get())).save(consumer, prefix("colorizer_full_pyramid_stonecutting"));

		FluroBlock.FLURO_BY_DYE.entrySet().stream().forEach((x) -> {
			FluroBlock b = x.getValue().get();
			ShapedRecipeBuilder.shaped(b, 4).define('G', Tags.Items.GLASS).define('L', DecorBlocks.ILLUMINATION_TUBE.get()).define('A', b.getColor().getTag()).pattern("GAG").pattern("ALA").pattern("GAG").unlockedBy("has_dye", has(Tags.Items.DYES)).unlockedBy("has_tube", has(DecorBlocks.ILLUMINATION_TUBE.get())).save(consumer);
		});

		ShapedRecipeBuilder.shaped(DecorItems.CHAIN_LINK.get(), 4).define('X', Tags.Items.INGOTS_IRON).define('N', Tags.Items.NUGGETS_IRON).pattern(" N ").pattern("NXN").pattern(" N ").unlockedBy("has_ingot", has(Tags.Items.INGOTS_IRON)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorItems.CHAIN_LINK.get(), 4).define('X', DecorTags.Items.INGOTS_STEEL).define('N', DecorTags.Items.NUGGETS_STEEL).pattern(" N ").pattern("NXN").pattern(" N ").unlockedBy("has_ingot", has(DecorTags.Items.INGOTS_STEEL)).save(consumer, prefix("chain_link_steel"));
		ShapedRecipeBuilder.shaped(DecorItems.CHAIN_LINK.get(), 4).define('X', DecorTags.Items.INGOTS_ALUMINUM).define('N', DecorTags.Items.NUGGETS_ALUMINUM).pattern(" N ").pattern("NXN").pattern(" N ").unlockedBy("has_ingot", has(DecorTags.Items.INGOTS_ALUMINUM)).save(consumer, prefix("chain_link_aluminum"));
		ShapedRecipeBuilder.shaped(DecorBlocks.CHAIN_LINK_FENCE.get(), 8).define('X', DecorItems.CHAIN_LINK.get()).pattern("XXX").pattern("XXX").unlockedBy("has_chain_link", has(DecorItems.CHAIN_LINK.get())).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.QUARTZ_DOOR.get(), 3).define('X', Items.QUARTZ).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_quartz", has(Items.QUARTZ)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.GLASS_DOOR.get(), 3).define('X', Tags.Items.GLASS).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_glass", has(Tags.Items.GLASS)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.STEEL_DOOR.get(), 3).define('X', DecorTags.Items.INGOTS_STEEL).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_steel", has(DecorTags.Items.INGOTS_STEEL)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.CHAIN_LINK_DOOR.get(), 3).define('X', DecorItems.CHAIN_LINK.get()).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_chain_link", has(DecorItems.CHAIN_LINK.get())).save(consumer);

		ShapedRecipeBuilder.shaped(DecorBlocks.FOUNTAIN.get()).define('X', Tags.Items.COBBLESTONE).define('W', DecorTags.Items.BUCKETS_WATER).define('I', DecorTags.Items.INGOTS_ALUMINUM).pattern("XIX").pattern("XWX").pattern("XIX").unlockedBy("has_ingot", has(DecorTags.Items.INGOTS_ALUMINUM)).save(consumer, prefix("fountain_aluminum"));
		ShapedRecipeBuilder.shaped(DecorBlocks.FOUNTAIN.get()).define('X', Tags.Items.COBBLESTONE).define('W', DecorTags.Items.BUCKETS_WATER).define('I', DecorTags.Items.INGOTS_STEEL).pattern("XIX").pattern("XWX").pattern("XIX").unlockedBy("has_ingot", has(DecorTags.Items.INGOTS_STEEL)).save(consumer, prefix("fountain_steel"));
		ShapedRecipeBuilder.shaped(DecorBlocks.FOUNTAIN.get()).define('X', Tags.Items.COBBLESTONE).define('W', DecorTags.Items.BUCKETS_WATER).define('I', Tags.Items.INGOTS_IRON).pattern("XIX").pattern("XWX").pattern("XIX").unlockedBy("has_ingot", has(Tags.Items.INGOTS_IRON)).save(consumer);

		ShapedRecipeBuilder.shaped(DecorBlocks.CALENDAR.get()).define('#', Items.PAPER).pattern("##").pattern("##").pattern("##").unlockedBy("has_paper", has(Items.PAPER)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.WALL_CLOCK.get()).define('#', ItemTags.PLANKS).define('C', Items.CLOCK).pattern("###").pattern("#C#").pattern("###").unlockedBy("has_clock", has(Items.CLOCK)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.WALL_CLOCK.get()).define('#', ItemTags.PLANKS).define('R', Tags.Items.DUSTS_REDSTONE).define('G', Tags.Items.INGOTS_GOLD).pattern("#G#").pattern("GRG").pattern("#G#").unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE)).unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD)).save(consumer, prefix("wall_clock_alt"));

		SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), DecorBlocks.DECORATIVE_STONE.get()).unlockedBy("has_stone", has(Blocks.STONE)).save(consumer, prefix("decorative_path_stonecutting"));
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), DecorBlocks.STONE_PATH.get()).unlockedBy("has_stone", has(Blocks.STONE)).save(consumer, prefix("stone_path_stonecutting"));
	}

	private ResourceLocation prefix(String name) {
		return new ResourceLocation(AssortedDecor.MODID, name);
	}

	private String name(Item i) {
		return ForgeRegistries.ITEMS.getKey(i).getPath();
	}

	private String name(Block i) {
		return ForgeRegistries.BLOCKS.getKey(i).getPath();
	}

	@Override
	public String getName() {
		return "Assorted Decor recipes";
	}
}

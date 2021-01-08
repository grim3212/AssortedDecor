package com.grim3212.assorted.decor.common.data;

import java.util.function.Consumer;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.item.DecorItems;
import com.grim3212.assorted.decor.common.lib.DecorTags;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.data.SingleItemRecipeBuilder;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class DecorRecipes extends RecipeProvider {

	public DecorRecipes(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shapedRecipe(DecorItems.WALLPAPER.get()).key('X', ItemTags.WOOL).key('#', Items.PAPER).patternLine("#X").patternLine("#X").patternLine("#X").addCriterion("has_paper", hasItem(Items.PAPER)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorItems.WOOD_FRAME.get()).key('X', ItemTags.PLANKS).patternLine("  X").patternLine(" X ").patternLine("X  ").addCriterion("has_planks", hasItem(ItemTags.PLANKS)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorItems.IRON_FRAME.get()).key('X', Tags.Items.INGOTS_IRON).patternLine("  X").patternLine(" X ").patternLine("X  ").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorItems.COLORIZER_BRUSH.get()).key('X', DecorBlocks.COLORIZER.get()).key('R', Tags.Items.RODS_WOODEN).key('S', Tags.Items.STRING).patternLine(" SX").patternLine(" RS").patternLine("R  ").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(DecorItems.COLORIZER_BRUSH.get()).addIngredient(DecorTags.Items.BUCKETS_WATER).addIngredient(DecorItems.COLORIZER_BRUSH.get()).addCriterion("has_brush", hasItem(DecorItems.COLORIZER_BRUSH.get())).build(consumer, prefix("clean_colorizer_brush"));
		ShapedRecipeBuilder.shapedRecipe(DecorItems.NEON_SIGN.get(), 3).key('X', Tags.Items.OBSIDIAN).key('G', ItemTags.PLANKS).key('C', Tags.Items.DUSTS_REDSTONE).patternLine("XXX").patternLine("XCX").patternLine(" G ").addCriterion("has_obsidian", hasItem(Tags.Items.OBSIDIAN)).build(consumer);

		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER.get(), 4).key('X', Tags.Items.STONE).key('R', Tags.Items.DYES_RED).key('G', Tags.Items.DYES_GREEN).key('B', Tags.Items.DYES_BLUE).key('D', Tags.Items.DYES).patternLine("XRX").patternLine("GDB").patternLine("XDX").addCriterion("has_stone", hasItem(Tags.Items.STONE)).addCriterion("has_dye", hasItem(Tags.Items.DYES)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_CHAIR.get(), 4).key('X', DecorBlocks.COLORIZER.get()).patternLine("X  ").patternLine("XXX").patternLine("X X").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_TABLE.get(), 4).key('X', DecorBlocks.COLORIZER.get()).patternLine("XXX").patternLine("X X").patternLine("X X").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_STOOL.get(), 4).key('X', DecorBlocks.COLORIZER.get()).key('S', Tags.Items.RODS_WOODEN).patternLine("XXX").patternLine("S S").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_COUNTER.get(), 4).key('X', DecorBlocks.COLORIZER.get()).key('S', Tags.Items.RODS_WOODEN).patternLine("XXX").patternLine(" S ").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_FENCE.get(), 4).key('X', DecorBlocks.COLORIZER.get()).key('S', Tags.Items.RODS_WOODEN).patternLine("XSX").patternLine("XSX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_FENCE_GATE.get(), 2).key('X', DecorBlocks.COLORIZER.get()).key('S', Tags.Items.RODS_WOODEN).patternLine("SXS").patternLine("SXS").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_WALL.get(), 6).key('X', DecorBlocks.COLORIZER.get()).patternLine(" X ").patternLine("XXX").patternLine("XXX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_STAIRS.get(), 4).key('X', DecorBlocks.COLORIZER.get()).patternLine("X  ").patternLine("XX ").patternLine("XXX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_SLAB.get(), 6).key('X', DecorBlocks.COLORIZER.get()).patternLine("XXX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_VERTICAL_SLAB.get(), 6).key('X', DecorBlocks.COLORIZER.get()).patternLine("X").patternLine("X").patternLine("X").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_DOOR.get(), 3).key('X', DecorBlocks.COLORIZER.get()).patternLine("XX").patternLine("XX").patternLine("XX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_TRAP_DOOR.get(), 2).key('X', DecorBlocks.COLORIZER.get()).patternLine("XXX").patternLine("XXX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_LAMP_POST.get(), 2).key('X', DecorBlocks.COLORIZER.get()).key('G', Blocks.GLOWSTONE).patternLine("XGX").patternLine("XXX").patternLine(" X ").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).addCriterion("has_glowstone", hasItem(Blocks.GLOWSTONE)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_SLOPE.get(), 4).key('X', DecorBlocks.COLORIZER.get()).patternLine("  X").patternLine(" XX").patternLine("XXX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_SLOPED_ANGLE.get(), 4).key('X', DecorBlocks.COLORIZER.get()).patternLine(" XX").patternLine("XXX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_SLOPED_INTERSECTION.get(), 4).key('X', DecorBlocks.COLORIZER.get()).patternLine("XX ").patternLine("X X").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_SLOPED_POST.get(), 4).key('X', DecorBlocks.COLORIZER.get()).patternLine("X ").patternLine("XX").patternLine("XX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_OBLIQUE_SLOPE.get(), 4).key('X', DecorBlocks.COLORIZER.get()).patternLine("  X").patternLine("XXX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_CORNER.get(), 4).key('X', DecorBlocks.COLORIZER.get()).patternLine("XXX").patternLine("XX ").patternLine("X  ").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_SLANTED_CORNER.get(), 4).key('X', DecorBlocks.COLORIZER.get()).patternLine("  X").patternLine("  X").patternLine("XXX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_PYRAMID.get(), 4).key('X', DecorBlocks.COLORIZER.get()).patternLine(" X ").patternLine("XXX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_FULL_PYRAMID.get(), 4).key('X', DecorBlocks.COLORIZER.get()).patternLine(" X ").patternLine(" X ").patternLine("XXX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_CHIMNEY.get(), 6).key('X', DecorBlocks.COLORIZER.get()).key('I', Tags.Items.INGOTS_IRON).patternLine("X X").patternLine("X X").patternLine("XIX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_FIREPIT.get(), 4).key('X', DecorBlocks.COLORIZER.get()).key('P', ItemTags.PLANKS).patternLine("XPX").patternLine("XXX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_FIREPIT_COVERED.get(), 4).key('X', DecorBlocks.COLORIZER.get()).key('I', Items.IRON_BARS).key('P', ItemTags.PLANKS).patternLine("III").patternLine("XPX").patternLine("XXX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).addCriterion("has_iron_bars", hasItem(Items.IRON_BARS)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_FIREPLACE.get(), 4).key('X', DecorBlocks.COLORIZER.get()).key('P', ItemTags.PLANKS).patternLine("XXX").patternLine("XPX").patternLine("XXX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_FIRERING.get(), 4).key('X', DecorBlocks.COLORIZER.get()).key('P', ItemTags.PLANKS).patternLine(" X ").patternLine("XPX").patternLine(" X ").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_STOVE.get(), 4).key('X', DecorBlocks.COLORIZER.get()).key('I', Items.IRON_BARS).key('P', ItemTags.PLANKS).patternLine("XXX").patternLine("IPI").patternLine("XXX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).addCriterion("has_iron_bars", hasItem(Items.IRON_BARS)).build(consumer);

		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.ILLUMINATION_TUBE.get(), 4).key('G', Tags.Items.GLASS).key('L', Tags.Items.DUSTS_GLOWSTONE).key('A', DecorTags.Items.INGOTS_ALUMINUM).patternLine(" A ").patternLine("GLG").patternLine(" A ").addCriterion("has_aluminum", hasItem(DecorTags.Items.INGOTS_ALUMINUM)).addCriterion("has_glowstone", hasItem(Tags.Items.DUSTS_GLOWSTONE)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.ILLUMINATION_TUBE.get(), 4).key('G', Tags.Items.GLASS).key('L', Tags.Items.DUSTS_GLOWSTONE).key('A', Tags.Items.INGOTS_IRON).patternLine(" A ").patternLine("GLG").patternLine(" A ").addCriterion("has_aluminum", hasItem(Tags.Items.INGOTS_IRON)).addCriterion("has_glowstone", hasItem(Tags.Items.DUSTS_GLOWSTONE)).build(consumer, prefix("illumination_tube_iron"));

		ShapedRecipeBuilder.shapedRecipe(DecorItems.UNFIRED_PLANTER_POT.get()).key('X', Items.CLAY_BALL).patternLine("X X").patternLine("XXX").addCriterion("has_clay", hasItem(Items.CLAY_BALL)).build(consumer);

		CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(DecorItems.UNFIRED_PLANTER_POT.get()), DecorBlocks.PLANTER_POT.get(), 0.35f, 200).addCriterion("has_unfired_planter_pot", hasItem(DecorItems.UNFIRED_PLANTER_POT.get())).build(consumer);

		SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_SLAB.get(), 2).addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer, prefix("colorizer_slab_stonecutting"));
		SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_VERTICAL_SLAB.get(), 2).addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer, prefix("colorizer_vertical_slab_stonecutting"));
		SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_STAIRS.get()).addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer, prefix("colorizer_stairs_stonecutting"));
		SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_WALL.get()).addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer, prefix("colorizer_walls_stonecutting"));
		SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_CHAIR.get()).addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer, prefix("colorizer_chair_stonecutting"));
		SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_TABLE.get()).addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer, prefix("colorizer_table_stonecutting"));
		SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_SLOPE.get()).addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer, prefix("colorizer_slope_stonecutting"));
		SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_SLOPED_ANGLE.get()).addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer, prefix("colorizer_sloped_angle_stonecutting"));
		SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_SLOPED_INTERSECTION.get()).addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer, prefix("colorizer_sloped_intersection_stonecutting"));
		SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_SLOPED_POST.get()).addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer, prefix("colorizer_sloped_post_stonecutting"));
		SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_OBLIQUE_SLOPE.get()).addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer, prefix("colorizer_oblique_slope_stonecutting"));
		SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_CORNER.get()).addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer, prefix("colorizer_corner_stonecutting"));
		SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_SLANTED_CORNER.get()).addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer, prefix("colorizer_slanted_corner_stonecutting"));
		SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_PYRAMID.get()).addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer, prefix("colorizer_pyramid_stonecutting"));
		SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromItems(DecorBlocks.COLORIZER.get()), DecorBlocks.COLORIZER_FULL_PYRAMID.get()).addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer, prefix("colorizer_full_pyramid_stonecutting"));

		for (Block b : DecorBlocks.fluroBlocks()) {
			ShapedRecipeBuilder.shapedRecipe(b, 4).key('G', Tags.Items.GLASS).key('L', DecorBlocks.ILLUMINATION_TUBE.get()).key('A', fromMaterialColor(b.getMaterialColor()).getTag()).patternLine("GAG").patternLine("ALA").patternLine("GAG").addCriterion("has_dye", hasItem(Tags.Items.DYES)).addCriterion("has_tube", hasItem(DecorBlocks.ILLUMINATION_TUBE.get())).build(consumer);
		}

		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.QUARTZ_DOOR.get(), 3).key('X', Items.QUARTZ).patternLine("XX").patternLine("XX").patternLine("XX").addCriterion("has_quartz", hasItem(Items.QUARTZ)).build(consumer);
	}

	private ResourceLocation prefix(String name) {
		return new ResourceLocation(AssortedDecor.MODID, name);
	}

	/*
	 * Not spending time right now to figure this out so just making it super easy
	 */
	private DyeColor fromMaterialColor(MaterialColor color) {
		switch (color.colorIndex) {
			case 15:
				return DyeColor.ORANGE;
			case 16:
				return DyeColor.MAGENTA;
			case 17:
				return DyeColor.LIGHT_BLUE;
			case 18:
				return DyeColor.YELLOW;
			case 19:
				return DyeColor.LIME;
			case 20:
				return DyeColor.PINK;
			case 21:
				return DyeColor.GRAY;
			case 22:
				return DyeColor.LIGHT_GRAY;
			case 23:
				return DyeColor.CYAN;
			case 24:
				return DyeColor.PURPLE;
			case 25:
				return DyeColor.BLUE;
			case 26:
				return DyeColor.BROWN;
			case 27:
				return DyeColor.GREEN;
			case 28:
				return DyeColor.RED;
			case 29:
				return DyeColor.BLACK;
			default:
				return DyeColor.WHITE;
		}
	}

	@Override
	public String getName() {
		return "Assorted Decor recipes";
	}
}

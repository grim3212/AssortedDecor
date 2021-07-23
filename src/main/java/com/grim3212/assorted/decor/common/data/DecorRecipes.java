package com.grim3212.assorted.decor.common.data;

import java.util.function.Consumer;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.item.DecorItems;
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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.Tags;

public class DecorRecipes extends RecipeProvider {

	public DecorRecipes(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
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

		ShapedRecipeBuilder.shaped(DecorItems.UNFIRED_PLANTER_POT.get()).define('X', Items.CLAY_BALL).pattern("X X").pattern("XXX").unlockedBy("has_clay", has(Items.CLAY_BALL)).save(consumer);

		SimpleCookingRecipeBuilder.smelting(Ingredient.of(DecorItems.UNFIRED_PLANTER_POT.get()), DecorBlocks.PLANTER_POT.get(), 0.35f, 200).unlockedBy("has_unfired_planter_pot", has(DecorItems.UNFIRED_PLANTER_POT.get())).save(consumer);

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

		for (Block b : DecorBlocks.fluroBlocks()) {
			ShapedRecipeBuilder.shaped(b, 4).define('G', Tags.Items.GLASS).define('L', DecorBlocks.ILLUMINATION_TUBE.get()).define('A', fromMaterialColor(b.defaultMaterialColor()).getTag()).pattern("GAG").pattern("ALA").pattern("GAG").unlockedBy("has_dye", has(Tags.Items.DYES)).unlockedBy("has_tube", has(DecorBlocks.ILLUMINATION_TUBE.get())).save(consumer);
		}

		ShapedRecipeBuilder.shaped(DecorBlocks.QUARTZ_DOOR.get(), 3).define('X', Items.QUARTZ).pattern("XX").pattern("XX").pattern("XX").unlockedBy("has_quartz", has(Items.QUARTZ)).save(consumer);

		ShapedRecipeBuilder.shaped(DecorBlocks.CALENDAR.get()).define('#', Items.PAPER).pattern("##").pattern("##").pattern("##").unlockedBy("has_paper", has(Items.PAPER)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.WALL_CLOCK.get()).define('#', ItemTags.PLANKS).define('C', Items.CLOCK).pattern("###").pattern("#C#").pattern("###").unlockedBy("has_clock", has(Items.CLOCK)).save(consumer);
		ShapedRecipeBuilder.shaped(DecorBlocks.WALL_CLOCK.get()).define('#', ItemTags.PLANKS).define('R', Tags.Items.DUSTS_REDSTONE).define('G', Tags.Items.INGOTS_GOLD).pattern("#G#").pattern("GRG").pattern("#G#").unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE)).unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD)).save(consumer, prefix("wall_clock_alt"));
	}

	private ResourceLocation prefix(String name) {
		return new ResourceLocation(AssortedDecor.MODID, name);
	}

	/*
	 * Not spending time right now to figure this out so just making it super easy
	 */
	private DyeColor fromMaterialColor(MaterialColor color) {
		switch (color.id) {
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

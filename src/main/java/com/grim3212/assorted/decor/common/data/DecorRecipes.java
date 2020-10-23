package com.grim3212.assorted.decor.common.data;

import java.util.function.Consumer;

import com.grim3212.assorted.decor.common.block.DecorBlocks;
import com.grim3212.assorted.decor.common.item.DecorItems;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
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
		ShapedRecipeBuilder.shapedRecipe(DecorItems.COLORIZER_BRUSH.get()).key('X', DecorBlocks.COLORIZER.get()).key('R', Tags.Items.RODS_WOODEN).key('S', Tags.Items.STRING).patternLine("R  ").patternLine(" RS").patternLine(" SX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);

		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER.get(), 4).key('X', Tags.Items.STONE).key('R', Tags.Items.DYES_RED).key('G', Tags.Items.DYES_GREEN).key('B', Tags.Items.DYES_BLUE).key('D', Tags.Items.DYES).patternLine("XRX").patternLine("GDB").patternLine("XDX").addCriterion("has_stone", hasItem(Tags.Items.STONE)).addCriterion("has_dye", hasItem(Tags.Items.DYES)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_CHAIR.get(), 4).key('X', DecorBlocks.COLORIZER.get()).patternLine("X  ").patternLine("XXX").patternLine("X X").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_TABLE.get(), 4).key('X', DecorBlocks.COLORIZER.get()).patternLine("XXX").patternLine("X X").patternLine("X X").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_STOOL.get(), 4).key('X', DecorBlocks.COLORIZER.get()).key('S', Tags.Items.RODS_WOODEN).patternLine("XXX").patternLine("S S").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_COUNTER.get(), 4).key('X', DecorBlocks.COLORIZER.get()).key('S', Tags.Items.RODS_WOODEN).patternLine("XXX").patternLine(" S ").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_FENCE.get(), 4).key('X', DecorBlocks.COLORIZER.get()).key('S', Tags.Items.RODS_WOODEN).patternLine("XSX").patternLine("XSX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_FENCE_GATE.get(), 2).key('X', DecorBlocks.COLORIZER.get()).key('S', Tags.Items.RODS_WOODEN).patternLine("SXS").patternLine("SXS").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_WALL.get(), 7).key('X', DecorBlocks.COLORIZER.get()).patternLine(" X ").patternLine("XXX").patternLine("XXX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_STAIRS.get(), 4).key('X', DecorBlocks.COLORIZER.get()).patternLine("X  ").patternLine("XX ").patternLine("XXX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(DecorBlocks.COLORIZER_SLAB.get(), 6).key('X', DecorBlocks.COLORIZER.get()).patternLine("XXX").addCriterion("has_colorizer", hasItem(DecorBlocks.COLORIZER.get())).build(consumer);
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
	}

	@Override
	public String getName() {
		return "Assorted Decor recipes";
	}
}

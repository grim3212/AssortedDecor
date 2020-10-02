package com.grim3212.assorted.decor.common.data;

import java.util.function.Consumer;

import com.grim3212.assorted.decor.common.item.DecorItems;

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
	}

	@Override
	public String getName() {
		return "Assorted Decor recipes";
	}
}

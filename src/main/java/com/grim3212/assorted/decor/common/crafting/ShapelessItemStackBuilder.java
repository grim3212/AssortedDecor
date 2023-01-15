package com.grim3212.assorted.decor.common.crafting;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

public class ShapelessItemStackBuilder {
	private final ItemStack result;
	private final List<Ingredient> ingredients = Lists.newArrayList();
	private final Advancement.Builder advancement = Advancement.Builder.advancement();
	@Nullable
	private String group;
	private final RecipeCategory category;

	ShapelessItemStackBuilder(ItemStack item, RecipeCategory category) {
		this.result = item;
		this.category = category;
	}

	public static ShapelessItemStackBuilder shapeless(ItemStack item, RecipeCategory category) {
		return new ShapelessItemStackBuilder(item, category);
	}

	public ShapelessItemStackBuilder requires(TagKey<Item> tag) {
		return this.requires(Ingredient.of(tag));
	}

	public ShapelessItemStackBuilder requires(ItemLike item) {
		return this.requires(item, 1);
	}

	public ShapelessItemStackBuilder requires(ItemLike item, int count) {
		for (int i = 0; i < count; ++i) {
			this.requires(Ingredient.of(item));
		}

		return this;
	}

	public ShapelessItemStackBuilder requires(Ingredient ingredient) {
		return this.requires(ingredient, 1);
	}

	public ShapelessItemStackBuilder requires(Ingredient ingredient, int count) {
		for (int i = 0; i < count; ++i) {
			this.ingredients.add(ingredient);
		}

		return this;
	}

	public ShapelessItemStackBuilder unlockedBy(String s, CriterionTriggerInstance trigger) {
		this.advancement.addCriterion(s, trigger);
		return this;
	}

	public ShapelessItemStackBuilder group(@Nullable String group) {
		this.group = group;
		return this;
	}

	public ItemStack getResult() {
		return this.result;
	}

	public void save(Consumer<FinishedRecipe> recipe) {
		this.save(recipe, getDefaultRecipeId(this.getResult().getItem()));
	}

	public ResourceLocation getDefaultRecipeId(ItemLike item) {
		return ForgeRegistries.ITEMS.getKey(item.asItem());
	}

	public void save(Consumer<FinishedRecipe> consumer, ResourceLocation location) {
		this.ensureValid(location);
		this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(location)).rewards(AdvancementRewards.Builder.recipe(location)).requirements(RequirementsStrategy.OR);
		consumer.accept(new ShapelessItemStackBuilder.Result(location, this.result, this.group == null ? "" : this.group, this.ingredients, this.advancement, new ResourceLocation(location.getNamespace(), "recipes/" + this.category.getFolderName() + "/" + location.getPath())));
	}

	private void ensureValid(ResourceLocation location) {
		if (this.advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + location);
		}
	}

	public static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final ItemStack result;
		private final String group;
		private final List<Ingredient> ingredients;
		private final Advancement.Builder advancement;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation location, ItemStack result, String group, List<Ingredient> ingredients, Advancement.Builder advancements, ResourceLocation advancementId) {
			this.id = location;
			this.result = result;
			this.group = group;
			this.ingredients = ingredients;
			this.advancement = advancements;
			this.advancementId = advancementId;
		}

		public void serializeRecipeData(JsonObject json) {
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}

			JsonArray jsonarray = new JsonArray();

			for (Ingredient ingredient : this.ingredients) {
				jsonarray.add(ingredient.toJson());
			}

			json.add("ingredients", jsonarray);
			JsonObject jsonobject = new JsonObject();

			jsonobject.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result.getItem()).toString());
			jsonobject.addProperty("count", this.result.getCount());
			if (this.result.hasTag())
				jsonobject.addProperty("nbt", this.result.getTag().toString());

			json.add("result", jsonobject);
		}

		public RecipeSerializer<?> getType() {
			return RecipeSerializer.SHAPELESS_RECIPE;
		}

		public ResourceLocation getId() {
			return this.id;
		}

		@Nullable
		public JsonObject serializeAdvancement() {
			return this.advancement.serializeToJson();
		}

		@Nullable
		public ResourceLocation getAdvancementId() {
			return this.advancementId;
		}
	}
}
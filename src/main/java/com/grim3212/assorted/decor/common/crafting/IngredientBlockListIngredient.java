package com.grim3212.assorted.decor.common.crafting;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public class IngredientBlockListIngredient extends Ingredient {

	private final Ingredient blockList;

	public IngredientBlockListIngredient(Stream<? extends Ingredient.Value> input, Ingredient blockList) {
		super(input);
		this.blockList = blockList;
	}

	public IngredientBlockListIngredient(TagKey<Item> input, TagKey<Item> blockList) {
		this(Stream.of(new Ingredient.TagValue(input)), Ingredient.of(blockList));
	}

	@Override
	public boolean test(ItemStack t) {
		if (t == null)
			return false;
		return !blockList.test(t) && super.test(t);
	}

	public Ingredient getBlockList() {
		return blockList;
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public JsonElement toJson() {
		JsonObject obj = new JsonObject();
		obj.addProperty("type", Serializer.ID.toString());
		obj.add("input", super.toJson());
		obj.add("blockList", this.blockList.toJson());
		return obj;
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return Serializer.INSTANCE;
	}

	@Override
	public ItemStack[] getItems() {
		return Arrays.stream(super.getItems()).filter((stack) -> !Arrays.stream(this.blockList.getItems()).anyMatch(x -> ItemStack.isSameIgnoreDurability(x, stack))).collect(Collectors.toList()).toArray(ItemStack[]::new);
	}

	public static class Serializer implements IIngredientSerializer<IngredientBlockListIngredient> {
		public static final Serializer INSTANCE = new Serializer();
		public static final ResourceLocation ID = new ResourceLocation("assorteddecor:blocklist");

		public IngredientBlockListIngredient parse(FriendlyByteBuf buffer) {
			return new IngredientBlockListIngredient(Stream.generate(() -> new Ingredient.ItemValue(buffer.readItem())).limit(buffer.readVarInt()), Ingredient.fromNetwork(buffer));
		}

		public IngredientBlockListIngredient parse(JsonObject json) {
			return new IngredientBlockListIngredient(Stream.of(Ingredient.valueFromJson(json.get("input").getAsJsonObject())), Ingredient.fromJson(json.get("blockList")));
		}

		public void write(FriendlyByteBuf buffer, IngredientBlockListIngredient ingredient) {
			ItemStack[] items = ingredient.getItems();
			buffer.writeVarInt(items.length);

			for (ItemStack stack : items)
				buffer.writeItem(stack);

			ingredient.toNetwork(buffer);
		}
	}
}
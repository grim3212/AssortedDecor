package com.grim3212.assorted.decor.client.data;

import com.grim3212.assorted.decor.AssortedDecor;
import com.grim3212.assorted.decor.common.item.DecorItems;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DecorItemModelProvider extends ItemModelProvider {

	public DecorItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, AssortedDecor.MODID, existingFileHelper);
	}

	@Override
	public String getName() {
		return "Assorted Decor item models";
	}

	@Override
	protected void registerModels() {
		generatedItem(DecorItems.WALLPAPER.get());
		generatedItem(DecorItems.WOOD_FRAME.get());
		generatedItem(DecorItems.IRON_FRAME.get());
		generatedItem(DecorItems.UNFIRED_PLANTER_POT.get());
	}

	private ItemModelBuilder generatedItem(String name) {
		return withExistingParent(name, "item/generated").texture("layer0", prefix("item/" + name));
	}

	private ItemModelBuilder generatedItem(Item i) {
		return generatedItem(name(i));
	}

	private static String name(Item i) {
		return Registry.ITEM.getKey(i).getPath();
	}

	private ResourceLocation prefix(String name) {
		return new ResourceLocation(AssortedDecor.MODID, name);
	}
}
